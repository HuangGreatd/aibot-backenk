package com.juzipi.springbootinit.producer;

import com.google.gson.Gson;
import com.juzipi.springbootinit.config.RabbitMqConfig;
import com.juzipi.springbootinit.mapper.ChatmessageMapper;
import com.juzipi.springbootinit.model.dto.rabbit.RabbitMessage;
import com.juzipi.springbootinit.model.entity.Chatmessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName RabbitMqConsumer
 * @Description: 消息队列消费者
 * @Author: 橘子皮
 * @CreateDate: 2025/3/28 15:56
 */
@Component
public class RabbitMqConsumer {

//    @RabbitListener(queues = RabbitMqConfig.QUEUE_NAME)
//    public void receiveMessage(String message) {
//        System.out.println("Received message: " + message);
//    }

    @Resource
    private ChatmessageMapper chatmessageMapper;

    /**
     * 数据处理展示
     * @param rabbitMessage
     */
    @RabbitListener(queues = RabbitMqConfig.QUEUE_NAME)
    public void receiveMessageObj(RabbitMessage rabbitMessage) {
        Long userId = rabbitMessage.getUserId();
        String content = rabbitMessage.getContent();
        String aiContext = rabbitMessage.getAiContext();
//        System.out.println("Received message: " + content + "  " + "aiContext:" + aiContext);
        //消息处理
        String handleContent = extractText(content);

        String pattern = "<think>.*?</think>";
        Pattern r = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher m = r.matcher(aiContext);
        String handleAiContent = m.replaceAll("").trim();

        Chatmessage chatmessage = new Chatmessage();
        chatmessage.setToMessage(handleAiContent);
        chatmessage.setFromMessage(handleContent);
        chatmessage.setFromUserId(userId);
        chatmessageMapper.insert(chatmessage);
    }


    public static String extractText(String content) {
        try {
            // 使用 Gson 尝试将 content 解析为 JSON
            Gson gson = new Gson();
            Map<String, String> jsonMap = gson.fromJson(content, Map.class);
            // 如果解析成功，获取 message 字段的值
            if (jsonMap != null && jsonMap.containsKey("message")) {
                return jsonMap.get("message");
            }
        } catch (Exception e) {
            // 解析失败，说明 content 是普通文本，直接返回
        }
        return content;
    }
}
