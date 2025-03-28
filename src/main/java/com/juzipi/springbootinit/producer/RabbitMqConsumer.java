package com.juzipi.springbootinit.producer;

import com.juzipi.springbootinit.config.RabbitMqConfig;
import com.juzipi.springbootinit.mapper.ChatmessageMapper;
import com.juzipi.springbootinit.model.dto.rabbit.RabbitMessage;
import com.juzipi.springbootinit.model.entity.Chatmessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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

    @RabbitListener(queues = RabbitMqConfig.QUEUE_NAME)
    public void receiveMessageObj(RabbitMessage rabbitMessage) {
        Long userId = rabbitMessage.getUserId();
        String content = rabbitMessage.getContent();
        String aiContext = rabbitMessage.getAiContext();
        System.out.println("Received message: " + content + "  " + "aiContext:" + aiContext);
        Chatmessage chatmessage = new Chatmessage();
        chatmessage.setToMessage(aiContext);
        chatmessage.setFromMessage(content);
        chatmessage.setFromUserId(userId);
        chatmessageMapper.insert(chatmessage);
    }
}
