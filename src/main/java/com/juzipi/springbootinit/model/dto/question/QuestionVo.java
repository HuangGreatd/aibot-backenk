package com.juzipi.springbootinit.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName QuestionVo
 * @Description:
 * @Author: 橘子皮
 * @CreateDate: 2025/3/19 20:43
 */
@Data
public class QuestionVo {
    /**
     * id
     */
    private Long id;

    /**
     * 问题
     */
    private String questionTitle;

    /**
     *
     */
    private Integer frequencyNum;



}
