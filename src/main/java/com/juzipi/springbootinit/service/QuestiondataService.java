package com.juzipi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.juzipi.springbootinit.model.dto.question.QuestionVo;
import com.juzipi.springbootinit.model.entity.Questiondata;

import java.util.List;

/**
* @author 73782
* @description 针对表【questiondata(问题)】的数据库操作Service
* @createDate 2025-03-19 20:40:56
*/
public interface QuestiondataService extends IService<Questiondata> {

    List<QuestionVo> questionList();
}
