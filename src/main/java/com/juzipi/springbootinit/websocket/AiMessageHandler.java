package com.juzipi.springbootinit.websocket;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.ObjectMapper;
import com.juzipi.springbootinit.common.ForbiddenWordsDetector;
import com.juzipi.springbootinit.manager.AIManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static com.juzipi.springbootinit.constant.WebSocketConstant.HEART_REQUEST_MESSAGE;

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

    // 自定义线程池
    @Resource
    private ThreadPoolTaskExecutor asyncTaskExecutor;

    @Resource
    private ForbiddenWordsDetector forbiddenWordsDetector;


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
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        String userMessage = message.getPayload();
        // 前置违禁词检测（使用高效的AC自动机）
//        if (forbiddenWordsDetector.containsForbiddenWords(userMessage)) {
//            String warning = "您的消息包含违禁内容，请修改后重试！";
//            if (session.isOpen()) {
//                synchronized (session) {  // 确保线程安全
//                    session.sendMessage(new TextMessage(warning));
//                }
//            }
//            log.warn("违禁词拦截: {}", userMessage);
//            return;  // 直接返回，不进入异步流程
//        }

        //心跳检测

        //提交到异步线程池处理
        asyncTaskExecutor.execute(() -> {
            try {
                String aiResponse = aiManager.getQuestionByAi(userMessage);
                if (session.isOpen()) { // 检查会话是否有效
                    synchronized (session) { // 确保线程安全
                        session.sendMessage(new TextMessage(aiResponse));
                        log.info("收到用户消息: {}, 回复: {}", userMessage, aiResponse);
                    }
                }
            } catch (IOException e) {
                log.error("回复消息失败", e);
            }
        });

//        String aiResponse = aiManager.getQuestionByAi(userMessage);
//        TextMessage response = new TextMessage(aiResponse);
//        session.sendMessage(response);
//        log.info("收到用户消息: {}, 回复: {}", userMessage, aiResponse);
    }

    //广播消息
    private void broadcastMessage(String message) {
//        if (CollUtil.isNotEmpty(sessions)){
//            new ObjectMapper()
//        }
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
