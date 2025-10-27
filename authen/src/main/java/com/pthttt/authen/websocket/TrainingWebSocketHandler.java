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

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("✅ WebSocket client connected: {}", session.getId());

        // Send initial connection message
        sendMessage(session, Map.of(
                "type", "status",
                "message", "Connected to training server",
                "stage", "CONNECTED",
                "progress", 0));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("🔌 WebSocket client disconnected: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming messages from client if needed
        log.info("📨 Received message from client: {}", message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("❌ WebSocket transport error: {}", exception.getMessage());
        sessions.remove(session);
    }

    /**
     * Broadcast message to all connected clients
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
     * Send message to specific session
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
     * Send STATUS message to all clients
     */
    public void sendStatus(String message, String stage, int progress) {
        broadcastMessage(Map.of(
                "type", "status",
                "message", message,
                "stage", stage,
                "progress", progress));
    }

    /**
     * Send INFO message to all clients
     */
    public void sendInfo(String message) {
        broadcastMessage(Map.of(
                "type", "info",
                "message", message));
    }

    /**
     * Send EPOCH message to all clients
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
     * Send COMPLETED message to all clients (with full data from Python)
     */
    public void sendCompleted(Map<String, Object> completedData) {
        // Ensure type is set to "completed"
        completedData.put("type", "completed");

        log.info("📤 Broadcasting COMPLETED message with keys: {}", completedData.keySet());
        broadcastMessage(completedData);
    }

    /**
     * Send COMPLETED message to all clients (legacy method for backward
     * compatibility)
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
     * Send ERROR message to all clients
     */
    public void sendError(String message) {
        broadcastMessage(Map.of(
                "type", "error",
                "message", message));
    }

    /**
     * Get number of active sessions
     */
    public int getActiveSessionCount() {
        return sessions.size();
    }
}
