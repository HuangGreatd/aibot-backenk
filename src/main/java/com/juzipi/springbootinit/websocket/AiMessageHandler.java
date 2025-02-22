package com.juzipi.springbootinit.websocket;

import com.juzipi.springbootinit.manager.AIManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName AiMessageHandler
 * @Description: 消息回复  websocket 处理器
 * @Author: 橘子皮
 * @CreateDate: 2025/2/21 19:51
 */
@Component
@Slf4j
public class AiMessageHandler extends TextWebSocketHandler {
    @Resource
    private AIManager aiManager;

    //保存所有连接的会话
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 连接建立成功
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        log.info("用户连接成功，会话 ID: {}", sessionId);
    }

    // 收到前端发送的消息，处理消息并返回 AI 回复
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        String userMessage = message.getPayload();
        String aiResponse = aiManager.getQuestionByAi(userMessage);
        TextMessage response = new TextMessage(aiResponse);
        session.sendMessage(response);
        log.info("收到用户消息: {}, 回复: {}", userMessage, aiResponse);
    }

    // 关闭连接
    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String sessionId = session.getId();
        sessions.remove(sessionId);
        log.info("用户连接关闭，会话 ID: {}", sessionId);
    }
}
