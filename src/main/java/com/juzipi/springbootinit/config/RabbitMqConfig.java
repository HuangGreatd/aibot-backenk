package com.juzipi.springbootinit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName RabbitMqConfig
 * @Description:
 * @Author: 橘子皮
 * @CreateDate: 2025/3/28 10:54
 */
@Configuration
public class RabbitMqConfig {
    public static final String EXCHANGE_NAME = "test-exchange";

    public static final String ROUTING_KEY = "test-routing-key";

    public static final String QUEUE_NAME = "test-queue";


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 创建队列
    @Bean
    public Queue queue() {
        /**
         * @param name 队列名字
         * @param durable 声明一个持久队列(该队列将在服务器重启后继续存在)，则为true
         */
        return new Queue(QUEUE_NAME, true);
    }


    // 创建交换机（Direct类型）
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // 绑定队列到交换机
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY);
    }

}
