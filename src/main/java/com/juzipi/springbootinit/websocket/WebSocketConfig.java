package com.juzipi.springbootinit.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;

/**
 * @ClassName WebSocketConfig
 * @Description:WebSocket 配置（定义连接）
 * @Author: 橘子皮
 * @CreateDate: 2025/2/21 19:49
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private AiMessageHandler aiMessageHandler;

//    @Bean
//    public HandshakeInterceptor wsHandshakeInterceptor() {
//        return new WsHandshakeInterceptor();
//    }
    @Resource
    private WsHandshakeInterceptor wsHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(aiMessageHandler, "/ws/ai/chat")
                .setAllowedOrigins("*")
                .addInterceptors(wsHandshakeInterceptor);
    }
}
