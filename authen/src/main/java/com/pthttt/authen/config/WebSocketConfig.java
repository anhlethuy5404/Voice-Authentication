package com.pthttt.authen.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.pthttt.authen.websocket.TrainingWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final TrainingWebSocketHandler trainingWebSocketHandler;

    public WebSocketConfig(TrainingWebSocketHandler trainingWebSocketHandler) {
        this.trainingWebSocketHandler = trainingWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(trainingWebSocketHandler, "/ws/training-updates")
                .setAllowedOrigins("*"); // In production, specify exact origins
    }
}
