package com.juzipi.springbootinit.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName ChatMessageVo  
 * @Description: 
 * @Author: 橘子皮	
 * @CreateDate: 2025/3/7 16:09	
 */
@Data
public class ChatMessageVo implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 发送者id
     */
    private Integer fromUserId;

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

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}
