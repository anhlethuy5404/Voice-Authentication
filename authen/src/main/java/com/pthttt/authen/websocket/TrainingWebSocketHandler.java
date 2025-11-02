package com.pthttt.authen.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class TrainingWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(TrainingWebSocketHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    /**
     * Được gọi khi một client kết nối WebSocket thành công
     * Thêm session vào danh sách và gửi thông báo kết nối ban đầu
     *
     * @param session - WebSocket session của client vừa kết nối
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("WebSocket client connected: {}", session.getId());

        // Send initial connection message
        sendMessage(session, Map.of(
                "type", "status",
                "message", "Connected to training server",
                "stage", "CONNECTED",
                "progress", 0));
    }

    /**
     * Được gọi khi một client ngắt kết nối WebSocket
     * Xóa session khỏi danh sách và ghi log
     *
     * @param session - WebSocket session của client vừa ngắt kết nối
     * @param status  - Trạng thái đóng kết nối
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("WebSocket client disconnected: {}", session.getId());
    }

    /**
     * Xử lý tin nhắn văn bản nhận được từ client
     * Có thể mở rộng để xử lý các yêu cầu từ client nếu cần
     *
     * @param session - WebSocket session gửi tin nhắn
     * @param message - Nội dung tin nhắn nhận được
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming messages from client if needed
        log.info("Received message from client: {}", message.getPayload());
    }

    /**
     * Xử lý lỗi vận chuyển (transport error) của WebSocket
     * Ghi log lỗi và xóa session bị lỗi khỏi danh sách
     *
     * @param session   - WebSocket session gặp lỗi
     * @param exception - Exception xảy ra
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error: {}", exception.getMessage());
        sessions.remove(session);
    }

    /**
     * Phát (broadcast) tin nhắn đến tất cả các client đang kết nối
     * Chuyển đổi Map thành JSON và gửi đến từng session
     * Tự động xóa các session không còn mở hoặc gặp lỗi
     *
     * @param data - Dữ liệu dạng Map để gửi (sẽ được chuyển thành JSON)
     */
    public void broadcastMessage(Map<String, Object> data) {
        String json;
        try {
            json = objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("Failed to serialize message: {}", e.getMessage());
            return;
        }

        TextMessage message = new TextMessage(json);

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    log.error("Failed to send message to session {}: {}", session.getId(), e.getMessage());
                    sessions.remove(session);
                }
            }
        }
    }

    /**
     * Gửi tin nhắn đến một session cụ thể
     * Chuyển đổi Map thành JSON và gửi đến session được chỉ định
     *
     * @param session - WebSocket session cần gửi tin nhắn
     * @param data    - Dữ liệu dạng Map để gửi (sẽ được chuyển thành JSON)
     */
    public void sendMessage(WebSocketSession session, Map<String, Object> data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            session.sendMessage(new TextMessage(json));
        } catch (Exception e) {
            log.error("Failed to send message: {}", e.getMessage());
        }
    }

    /**
     * Gửi tin nhắn loại STATUS đến tất cả client
     * Sử dụng để thông báo trạng thái và tiến độ của quá trình training
     *
     * @param message  - Nội dung thông báo trạng thái
     * @param stage    - Giai đoạn hiện tại (VD: "PREPARING", "TRAINING", "TESTING")
     * @param progress - Phần trăm tiến độ (0-100)
     */
    public void sendStatus(String message, String stage, int progress) {
        broadcastMessage(Map.of(
                "type", "status",
                "message", message,
                "stage", stage,
                "progress", progress));
    }

    /**
     * Gửi tin nhắn loại INFO đến tất cả client
     * Sử dụng để gửi các thông tin chung không phải trạng thái
     *
     * @param message - Nội dung thông tin cần gửi
     */
    public void sendInfo(String message) {
        broadcastMessage(Map.of(
                "type", "info",
                "message", message));
    }

    /**
     * Gửi tin nhắn loại EPOCH đến tất cả client
     * Sử dụng để cập nhật kết quả sau mỗi epoch training
     *
     * @param epoch       - Số epoch hiện tại
     * @param totalEpochs - Tổng số epoch
     * @param trainLoss   - Loss trên tập training
     * @param valLoss     - Loss trên tập validation
     * @param trainAcc    - Độ chính xác trên tập training
     * @param valAcc      - Độ chính xác trên tập validation
     */
    public void sendEpoch(int epoch, int totalEpochs, double trainLoss, double valLoss, double trainAcc,
                          double valAcc) {
        broadcastMessage(Map.of(
                "type", "epoch",
                "epoch", epoch,
                "total_epochs", totalEpochs,
                "train_loss", trainLoss,
                "val_loss", valLoss,
                "train_acc", trainAcc,
                "val_acc", valAcc));
    }

    /**
     * Gửi tin nhắn loại COMPLETED đến tất cả client (với đầy đủ dữ liệu từ Python)
     * Phương thức được khuyến nghị để gửi toàn bộ dữ liệu training đã hoàn thành
     * Bao gồm: model_id, hyperparameters, train_logs, test_results, timestamps,
     * duration
     *
     * @param completedData - Map chứa toàn bộ dữ liệu training hoàn thành từ Python
     */
    public void sendCompleted(Map<String, Object> completedData) {
        // Ensure type is set to "completed"
        completedData.put("type", "completed");

        log.info("Broadcasting COMPLETED message with keys: {}", completedData.keySet());
        broadcastMessage(completedData);
    }

    /**
     * Gửi tin nhắn loại COMPLETED đến tất cả client (phương thức cũ)
     *
     * @deprecated Sử dụng phương thức sendCompleted(Map<String, Object>) thay thế
     *             Phương thức này chỉ giữ lại để tương thích ngược với code cũ
     * @param message        - Thông báo hoàn thành
     * @param bestValLoss    - Loss validation tốt nhất
     * @param finalEpoch     - Epoch cuối cùng
     * @param checkpointPath - Đường dẫn lưu checkpoint
     * @param testResults    - Kết quả test (Map chứa các metrics)
     */
    @Deprecated
    public void sendCompleted(String message, double bestValLoss, int finalEpoch, String checkpointPath,
                              Map<String, Double> testResults) {
        Map<String, Object> data = new ConcurrentHashMap<>();
        data.put("type", "completed");
        data.put("message", message);
        data.put("best_val_loss", bestValLoss);
        data.put("final_epoch", finalEpoch);
        data.put("checkpoint_path", checkpointPath);
        data.put("test_results", testResults);

        broadcastMessage(data);
    }

    /**
     * Gửi tin nhắn loại ERROR đến tất cả client
     * Sử dụng để thông báo lỗi xảy ra trong quá trình training
     *
     * @param message - Nội dung thông báo lỗi
     */
    public void sendError(String message) {
        broadcastMessage(Map.of(
                "type", "error",
                "message", message));
    }

    /**
     * Lấy số lượng session đang hoạt động
     *
     * @return Số lượng client đang kết nối WebSocket
     */
    public int getActiveSessionCount() {
        return sessions.size();
    }
}
