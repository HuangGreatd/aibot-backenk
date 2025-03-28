package com.juzipi.springbootinit.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @ClassName DelayQueueUtil
 * @Description:
 * @Author: 橘子皮
 * @CreateDate: 2025/3/28 15:29
 */
@Component
public class DelayQueueUtil {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void addToDelayQueue(String message) {
        redisTemplate.opsForZSet().add("delayQueue", message, System.currentTimeMillis() + 5);
    }

    public String pollFromDelayQueue() {
        Set<String> messages = redisTemplate.opsForZSet().rangeByScore("delayQueue", 0, System.currentTimeMillis(), 0, 1);
        if (!messages.isEmpty()) {
            String message = (String) messages.toArray()[0];
            redisTemplate.opsForZSet().remove("delayQueue", message);
            return message;
        }
        return null;
    }
}
