package com.juzipi.springbootinit.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName MessageVo
 * @Description:
 * @Author: 橘子皮
 * @CreateDate: 2025/3/20 8:39
 */
@Data
public class MessageVO {
    /**
     * id
     */
    private Long id;

    /**
     * 发送者id
     */
    private Long fromUserId;

    /**
     * 发送者消息
     */
    private String fromMessage;

    /**
     * AI回复消息
     */
    private String toMessage;

    /**
     * 创建时间
     */
    private Date createTime;

}
