package com.juzipi.springbootinit.model.dto.ai;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName AiResponseMessage
 * @Description: AI 回复
 * @Author: 橘子皮
 * @CreateDate: 2025/2/21 19:11
 */
@Data
public class AiResponseMessage {
    private Integer id;

    private String responseMessage;

    private Date time;
}
