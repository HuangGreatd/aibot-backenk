package com.juzipi.springbootinit.websocket;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.hutool.core.collection.CollUtil;
import com.google.gson.Gson;
import com.juzipi.springbootinit.constant.UserConstant;
import com.juzipi.springbootinit.manager.AIManager;
import com.juzipi.springbootinit.model.dto.rabbit.RabbitMessage;
import com.juzipi.springbootinit.producer.RabbitMqProducer;
import com.juzipi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
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

    // 自定义线程池
    @Resource
    private ThreadPoolTaskExecutor asyncTaskExecutor;

    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    //保存所有连接的会话
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    //保存所有连接会话id 和 用户id
    private final ConcurrentHashMap<String,Long> userIdMap = new ConcurrentHashMap<>();

    @Resource
    private RabbitMqProducer producer;

    // 连接建立成功
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        String tokenValue =(String) session.getAttributes().get("tokenValue");
        String sessionId = session.getId();
        SaTokenInfo saTokenInfo = getSaTokenInfo(tokenValue);
        Long loginId = null;
        if (saTokenInfo.getLoginId()!=null){
             loginId = Long.parseLong((String) saTokenInfo.getLoginId());
        }
        sessions.put(sessionId, session);
        assert loginId != null;
        userIdMap.put(sessionId, loginId);
        log.info("用户连接成功，会话 ID: {}", sessionId);
        log.info("用户连接成功，用户 ID: {}", loginId);
    }

    private SaTokenInfo getSaTokenInfo(String sessionId) {
        String RedisKey =  UserConstant.USER_LOGIN_STATE + ":" + sessionId;
        String tokenInfoStr = stringRedisTemplate.opsForValue().get(RedisKey);
        Gson gson = new Gson();
        SaTokenInfo saTokenInfo = gson.fromJson(tokenInfoStr, SaTokenInfo.class);
        return gson.fromJson(tokenInfoStr, SaTokenInfo.class);
    }

    // 收到前端发送的消息，处理消息并返回 AI 回复
    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
       //1.登录态失效校验
        String tokenValue =(String) session.getAttributes().get("tokenValue");
        SaTokenInfo saTokenInfo = getSaTokenInfo(tokenValue);
        if (saTokenInfo == null){
            if (session.isOpen()) { // 检查会话是否有效
                synchronized (session) { // 确保线程安全
                    session.sendMessage(new TextMessage("404"));
                    log.info("用户态过期", "发送代码404");
                }
            }
            return;
        }
        String userMessage = message.getPayload();
        String sessionId = session.getId();
        Long userId = userIdMap.get(sessionId);
        //提交到异步线程池处理
        asyncTaskExecutor.execute(() -> {
            try {
                String aiResponse = aiManager.getQuestionByAi(userMessage);
                if (session.isOpen()) { // 检查会话是否有效
                    synchronized (session) { // 确保线程安全
                        session.sendMessage(new TextMessage(aiResponse));
                        log.info("收到 用户消息: {}, 回复: {}", userMessage, aiResponse);
                    }
                }
                RabbitMessage rabbitMessage = new RabbitMessage();
                rabbitMessage.setUserId(userId);
                rabbitMessage.setContent(userMessage);
                rabbitMessage.setAiContext(aiResponse);
                producer.sendObjectMessage(rabbitMessage);
            } catch (IOException e) {
                log.error("回复消息失败", e);
            }
        });
    }

    //广播消息
    private void broadcastMessage(String message) {
        if (CollUtil.isNotEmpty(sessions)) {
            sessions.forEach((sessionId, session) -> {
                try {
                    if (session.isOpen()) {
                        synchronized (session) {
                            session.sendMessage(new TextMessage(message));
                        }
                    }
                } catch (Exception e) {
                    log.error("广播消息到会话 ID: {} 失败", sessionId, e);
                }
            });
        }
    }

    // 关闭连接
    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String sessionId = session.getId();
        sessions.remove(sessionId);
        userIdMap.remove(sessionId);
        log.info("用户连接关闭，会话 ID: {}", sessionId);
        log.info("用户连接关闭，用户 ID: {}", sessionId);
    }
}
