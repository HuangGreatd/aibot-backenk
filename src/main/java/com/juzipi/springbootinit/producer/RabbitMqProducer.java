package com.juzipi.springbootinit.producer;

import com.juzipi.springbootinit.config.RabbitMqConfig;
import com.juzipi.springbootinit.model.dto.rabbit.RabbitMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName RabbitMqProducer
 * @Description: 消息生产者
 * @Author: 橘子皮
 * @CreateDate: 2025/3/28 15:49
 */
@Service
public class RabbitMqProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, RabbitMqConfig.ROUTING_KEY, message);
    }
    public void sendObjectMessage(RabbitMessage message) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, RabbitMqConfig.ROUTING_KEY, message);
    }
}
