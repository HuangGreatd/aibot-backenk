package com.juzipi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzipi.springbootinit.model.dto.question.QuestionVo;
import com.juzipi.springbootinit.model.entity.Questiondata;
import com.juzipi.springbootinit.service.QuestiondataService;
import com.juzipi.springbootinit.mapper.QuestiondataMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 73782
 * @description 针对表【questiondata(问题)】的数据库操作Service实现
 * @createDate 2025-03-19 20:40:56
 */
@Service
public class QuestiondataServiceImpl extends ServiceImpl<QuestiondataMapper, Questiondata>
        implements QuestiondataService {

    @Resource
    private QuestiondataMapper questiondataMapper;

    @Override
    public List<QuestionVo> questionList() {
        List<Questiondata> randomQuestions = questiondataMapper.findRandomQuestions();
        List<QuestionVo> questionVoList = randomQuestions.stream().map(questiondata -> {
            QuestionVo questionVo = new QuestionVo();
            questionVo.setId(questiondata.getId());
            questionVo.setQuestionTitle(questiondata.getQuestionTitle());
            questionVo.setFrequencyNum(questiondata.getFrequencyNum());
            return questionVo;
        }).collect(Collectors.toList());
        return questionVoList;
    }
}




