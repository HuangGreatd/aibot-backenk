package com.juzipi.springbootinit.model.dto.ai;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName AiRequestMessage
 * @Description: 用户发送消息请求
 * @Author: 橘子皮
 * @CreateDate: 2025/2/21 19:08
 */
@Data
public class AiRequestMessage {
    /**
     * 用户id
     */
    private Integer id;


    /**
     * 问题
     */
    private String question;

    /**
     * 请求时间
     */
    private Date time;
}
