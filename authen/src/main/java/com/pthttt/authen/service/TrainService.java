package com.pthttt.authen.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pthttt.authen.model.Hyperparameter;
import com.pthttt.authen.model.Score;
import com.pthttt.authen.model.TrainLog;
import com.pthttt.authen.model.TrainRun;
import com.pthttt.authen.repository.*;
import com.pthttt.authen.websocket.TrainingWebSocketHandler;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class TrainService {

    @Autowired
    private TrainRunRepository trainRunRepository;

    @Autowired
    private TrainLogRepository trainLogRepository;

    @Autowired
    private HyperparameterRepository hyperparameterRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private ModelRepository modelRepository;
    private static final Logger log = LoggerFactory.getLogger(TrainService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TrainingWebSocketHandler webSocketHandler;

    public TrainService(TrainingWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    public int findMaxVersionByModelId(int modelId) {
        return trainRunRepository.findMaxVersionByModelId(modelId);
    }

    public Map<String, Object> prepareDataset(List<Map<String, String>> data, int trainPercent, int valPercent, String modelType) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Voice dataset is empty!");
        }

        // Trộn dữ liệu
        Collections.shuffle(data);

        // Chia train/val/test
        int splitIndexTrain = (int) (data.size() * trainPercent / 100.0);
        int splitIndexVal = (int) (data.size() * valPercent / 100.0);
        List<Map<String, String>> trainData = new ArrayList<>(data.subList(0, splitIndexTrain));
        List<Map<String, String>> valData = new ArrayList<>(data.subList(splitIndexTrain, splitIndexTrain + splitIndexVal));
        List<Map<String, String>> testData = new ArrayList<>(data.subList(splitIndexTrain + splitIndexVal, data.size()));

        // Lấy danh sách user_id duy nhất
        List<String> uniqueIds = data.stream()
                .map(v -> v.get("user_id"))
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Ánh xạ user_id → label (0,1,2,...)

        Map<String, Integer> idToLabel = new HashMap<>();
        for (int i = 0; i < uniqueIds.size(); i++) {
            idToLabel.put(uniqueIds.get(i), i);
        }

        // Áp dụng ánh xạ, đồng thời loại bỏ user_id
        Consumer<List<Map<String, String>>> remapAndClean = (list) -> {
            for (Map<String, String> item : list) {
                String uid = item.get("user_id");
                if (modelType.equals("embedding")) {
                    item.put("label", String.valueOf(idToLabel.get(uid)));
                }
                else {
                    item.put("label", item.get("real_voice"));
                }
                item.remove("user_id");
            }
        };

        remapAndClean.accept(trainData);
        remapAndClean.accept(valData);
        remapAndClean.accept(testData);

        // Trả kết quả cuối cùng
        return Map.of(
                "train_data", trainData,
                "val_data", valData,
                "test_data", testData,
                "num_classes", uniqueIds.size(),
                "label_map", idToLabel);
    }

    @Transactional
    public String saveTrainingResult(Map<String, Object> data) {
        try {
            log.info("Data: {}", data);
            // 1️ Lưu TrainRun
            TrainRun trainRun = new TrainRun();

            // Parse version từ save_dir
            String saveDir = (String) data.get("save_dir");
            String[] tmp = saveDir.split("/");

            int version = Integer.parseInt(tmp[tmp.length-2].substring(tmp[tmp.length-2].length()-1));
            trainRun.setVersion(version);

            trainRun.setStatus("COMPLETED");
            trainRun.setFilePath(saveDir);

            // Parse timestamps từ String
            String startedAtStr = (String) data.get("started_at");
            trainRun.setStartedAt(LocalDateTime.parse(startedAtStr));

            String finishedAtStr = (String) data.get("finished_at");
            trainRun.setFinishedAt(LocalDateTime.parse(finishedAtStr));

            trainRun.setModel(modelRepository.getModelById((int) data.get("model_id")));
            trainRun.setType((String) data.get("model_type"));


            trainRunRepository.save(trainRun);
            log.info("Saved TrainRun: id={}, version={}", trainRun.getId(), version);

            // 2️ Lưu TrainLogs
            List<Map<String, Object>> trainLogs = (List<Map<String, Object>>) data.get("train_logs");
            if (trainLogs != null && !trainLogs.isEmpty()) {
                for (Map<String, Object> trLog : trainLogs) {
                    TrainLog trainLog = new TrainLog();
                    trainLog.setTrainRun(trainRun);
                    trainLog.setEpoch((int) trLog.get("epoch"));
                    trainLog.setTrainAccuracy(((Number) trLog.get("train_accuracy")).floatValue());
                    trainLog.setTrainLoss(((Number) trLog.get("train_loss")).floatValue());
                    trainLog.setValAccuracy(((Number) trLog.get("val_accuracy")).floatValue());
                    trainLog.setValLoss(((Number) trLog.get("val_loss")).floatValue());

                    trainLogRepository.save(trainLog);
                }
                log.info("Saved {} TrainLogs", trainLogs.size());
            }

            // 3️ Lưu Hyperparameters
            Map<String, Object> hyperparameters = (Map<String, Object>) data.get("hyperparameters");
            if (hyperparameters != null && !hyperparameters.isEmpty()) {
                for (Map.Entry<String, Object> entry : hyperparameters.entrySet()) {
                    Hyperparameter hy = new Hyperparameter();
                    hy.setName(entry.getKey());
                    hy.setValue(String.valueOf(entry.getValue())); // Convert to String
                    hy.setCreatedAt(LocalDateTime.now());
                    hy.setTrainRun(trainRun);

                    hyperparameterRepository.save(hy);
                }
                log.info("Saved {} Hyperparameters", hyperparameters.size());
            }

            // 4️ Lưu Score
            Map<String, Object> testResults = (Map<String, Object>) data.get("test_results");
            if (testResults != null) {
                Score score = new Score();
                score.setAccuracy(((Number) testResults.get("accuracy")).floatValue());
                score.setPrecision(((Number) testResults.get("precision")).floatValue());
                score.setRecall(((Number) testResults.get("recall")).floatValue());
                score.setF1(((Number) testResults.get("f1")).floatValue());
                score.setTrainRun(trainRun);

                scoreRepository.save(score);
                log.info("Saved Score");
            }

            log.info("🎉 Training result saved successfully!");
            return "SUCCESS";

        } catch (Exception e) {
            log.error("Failed to save training result: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save training result: " + e.getMessage());
        }
    }

    /**
     * Bắt đầu huấn luyện (bất đồng bộ).
     */
    @Async
    public CompletableFuture<Void> startTrainingAsync(String pythonWsUrl, String jsonConfig,
            TrainingListener listener) {
        return CompletableFuture.runAsync(() -> {
            try {
                startTraining(pythonWsUrl, jsonConfig, listener);
            } catch (Exception e) {
                log.error("❌ Training failed: {}", e.getMessage(), e);
                if (listener != null)
                    listener.onError("Training failed: " + e.getMessage());
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
                if (listener != null)
                    listener.onStatus("Connected and sent config to Python.");

                // Send updates to frontend WebSocket
                webSocketHandler.sendStatus("Connected to Python server", "CONNECTED", 10);
                webSocketHandler.sendInfo("Training configuration sent to Python server");
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                String payload = message.getPayload();

                try {
                    JsonNode json = objectMapper.readTree(payload);
                    String type = json.path("type").asText("");

                    switch (type) {
                        case "status" -> {
                            String msg = json.path("message").asText();
                            String stage = json.path("stage").asText("UNKNOWN");
                            int progress = json.path("progress").asInt(0);

                            if (listener != null)
                                listener.onStatus(msg);
                            webSocketHandler.sendStatus(msg, stage.toUpperCase(), progress);
                        }
                        case "info" -> {
                            String msg = json.path("message").asText();
                            if (listener != null)
                                listener.onInfo(msg);
                            webSocketHandler.sendInfo(msg);
                        }
                        case "epoch" -> {
                            int epoch = json.path("epoch").asInt();
                            int totalEpochs = json.path("total_epochs").asInt();
                            double trainLoss = json.path("train_loss").asDouble();
                            double valLoss = json.path("val_loss").asDouble();
                            double trainAcc = json.path("train_acc").asDouble(0.0);
                            double valAcc = json.path("val_acc").asDouble(0.0);

                            if (listener != null)
                                listener.onEpoch(epoch, totalEpochs, trainLoss, valLoss);
                            webSocketHandler.sendEpoch(epoch, totalEpochs, trainLoss, valLoss, trainAcc, valAcc);
                        }
                        case "completed" -> {
                            // ✅ Convert entire JSON to Map to preserve all fields
                            Map<String, Object> completedData = objectMapper.convertValue(json, Map.class);

                            // Log để debug
                            log.info("📦 Received COMPLETED data with keys: {}", completedData.keySet());

                            String msg = json.path("message").asText("Training completed");

                            if (listener != null)
                                listener.onCompleted(msg);

                            // ✅ Send full data to WebSocket clients
                            webSocketHandler.sendCompleted(completedData);
                            latch.countDown();
                        }
                        case "error" -> {
                            String msg = json.path("message").asText();
                            if (listener != null)
                                listener.onError(msg);
                            webSocketHandler.sendError(msg);
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
                if (listener != null)
                    listener.onError("Transport error: " + ex.getMessage());
                webSocketHandler.sendError("Connection error: " + ex.getMessage());
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
