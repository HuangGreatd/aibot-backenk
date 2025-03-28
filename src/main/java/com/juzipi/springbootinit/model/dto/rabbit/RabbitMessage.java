package com.juzipi.springbootinit.model.dto.rabbit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName RabbitMessage
 * @Description:
 * @Author: 橘子皮
 * @CreateDate: 2025/3/28 16:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RabbitMessage implements Serializable {

    //用户ID
    private Long userId;

    //用户发送消息
    private String content;

    //AI响应消息
    private String AiContext;

}
