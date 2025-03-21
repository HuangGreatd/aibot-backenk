package com.juzipi.springbootinit.mapper;

import com.juzipi.springbootinit.model.entity.Questiondata;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 73782
* @description 针对表【questiondata(问题)】的数据库操作Mapper
* @createDate 2025-03-19 20:40:56
* @Entity com.juzipi.springbootinit.model.entity.Questiondata
*/
public interface QuestiondataMapper extends BaseMapper<Questiondata> {


    List<Questiondata> findRandomQuestions();
}




