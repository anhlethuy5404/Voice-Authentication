package com.pthttt.authen.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class TrainService {

    private static final Logger log = LoggerFactory.getLogger(TrainService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Tách dữ liệu train/val theo tỉ lệ phần trăm.
     */
    public Map<String, Object> prepareDataset(List<Map<String, String>> data, int trainPercent) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Voice dataset is empty!");
        }

        // 1️⃣ Trộn dữ liệu
        Collections.shuffle(data);

        // 2️⃣ Chia train/val
        int splitIndex = (int) (data.size() * trainPercent / 100.0);
        List<Map<String, String>> trainData = new ArrayList<>(data.subList(0, splitIndex));
        List<Map<String, String>> valData = new ArrayList<>(data.subList(splitIndex, data.size()));

        // 3️⃣ Lấy danh sách user_id duy nhất
        List<String> uniqueIds = data.stream()
                .map(v -> v.get("user_id"))
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // 4️⃣ Ánh xạ user_id → label (0,1,2,...)
        Map<String, Integer> idToLabel = new HashMap<>();
        for (int i = 0; i < uniqueIds.size(); i++) {
            idToLabel.put(uniqueIds.get(i), i);
        }

        // 5️⃣ Áp dụng ánh xạ, đồng thời loại bỏ user_id
        Consumer<List<Map<String, String>>> remapAndClean = (list) -> {
            for (Map<String, String> item : list) {
                String uid = item.get("user_id");
                item.put("label", String.valueOf(idToLabel.get(uid)));
                item.remove("user_id");
            }
        };

        remapAndClean.accept(trainData);
        remapAndClean.accept(valData);

        // 6️⃣ Trả kết quả cuối cùng
        return Map.of(
                "train_data", trainData,
                "val_data", valData,
                "num_classes", uniqueIds.size(),
                "label_map", idToLabel
        );
    }



    /**
     * Bắt đầu huấn luyện (bất đồng bộ).
     */
    @Async
    public CompletableFuture<Void> startTrainingAsync(String pythonWsUrl, String jsonConfig, TrainingListener listener) {
        return CompletableFuture.runAsync(() -> {
            try {
                startTraining(pythonWsUrl, jsonConfig, listener);
            } catch (Exception e) {
                log.error("❌ Training failed: {}", e.getMessage(), e);
                if (listener != null) listener.onError("Training failed: " + e.getMessage());
            }
        });
    }

    /**
     * Gửi JSON config trực tiếp tới Python WebSocket server.
     */
    private void startTraining(String pythonWsUrl, String jsonConfig, TrainingListener listener) throws Exception {
        // ✅ Kiểm tra JSON hợp lệ trước khi gửi
        objectMapper.readTree(jsonConfig);

        CountDownLatch latch = new CountDownLatch(1);
        WebSocketSession[] sessionHolder = new WebSocketSession[1];

        StandardWebSocketClient client = new StandardWebSocketClient();
        client.doHandshake(new AbstractWebSocketHandler() {

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                log.info("✅ Connected to Python WebSocket server.");
                sessionHolder[0] = session;
                session.sendMessage(new TextMessage(jsonConfig));
                if (listener != null) listener.onStatus("Connected and sent config to Python.");
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                String payload = message.getPayload();

                try {
                    JsonNode json = objectMapper.readTree(payload);
                    String type = json.path("type").asText("");

                    switch (type) {
                        case "status" -> listener.onStatus(json.path("message").asText());
                        case "info" -> listener.onInfo(json.path("message").asText());
                        case "epoch" -> listener.onEpoch(
                                json.path("epoch").asInt(),
                                json.path("total_epochs").asInt(),
                                json.path("train_loss").asDouble(),
                                json.path("val_loss").asDouble()
                        );
                        case "completed" -> {
                            listener.onCompleted(json.path("message").asText());
                            latch.countDown();
                        }
                        case "error" -> {
                            listener.onError(json.path("message").asText());
                            latch.countDown();
                        }
                        default -> log.warn("[UNKNOWN MESSAGE] {}", payload);
                    }

                } catch (Exception e) {
                    log.warn("⚠️ Invalid JSON from Python: {}", e.getMessage());
                }
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable ex) throws Exception {
                log.error("❌ WebSocket error: {}", ex.getMessage());
                if (listener != null) listener.onError("Transport error: " + ex.getMessage());
                latch.countDown();
            }

        }, pythonWsUrl).get();

        // Chờ đến khi Python gửi “completed” hoặc “error”
        latch.await();

        if (sessionHolder[0] != null && sessionHolder[0].isOpen()) {
            sessionHolder[0].close();
        }

        log.info("✅ Training session closed.");
    }

    /**
     * Callback interface nhận tiến trình huấn luyện.
     */
    public interface TrainingListener {
        void onStatus(String message);
        void onInfo(String message);
        void onEpoch(int epoch, int total, double trainLoss, double valLoss);
        void onCompleted(String message);
        void onError(String message);
    }
}
