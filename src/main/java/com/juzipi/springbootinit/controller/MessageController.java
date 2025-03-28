package com.juzipi.springbootinit.controller;

import com.juzipi.springbootinit.model.dto.rabbit.RabbitMessage;
import com.juzipi.springbootinit.producer.RabbitMqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName MessageController  
 * @Description: 
 * @Author: 橘子皮	
 * @CreateDate: 2025/3/28 15:49	
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private RabbitMqProducer producer;

    @GetMapping("/send")
    public String sendMessage(@RequestParam String msg) {
        producer.sendMessage(msg);
        return "Message sent: " + msg;
    }

//    @GetMapping("/sendObj")
//    public String sendMessageObj(@RequestParam Integer id,@RequestParam String content) {
//        RabbitMessage customMessage = new RabbitMessage(content, id);
//        producer.sendObjectMessage(customMessage);
//        return "Message sent: " + customMessage;
//    }

}
