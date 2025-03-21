package com.juzipi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzipi.springbootinit.annotation.AuthCheck;
import com.juzipi.springbootinit.common.BaseResponse;
import com.juzipi.springbootinit.common.DeleteRequest;
import com.juzipi.springbootinit.common.ErrorCode;
import com.juzipi.springbootinit.common.ResultUtils;
import com.juzipi.springbootinit.config.WxOpenConfig;
import com.juzipi.springbootinit.constant.UserConstant;
import com.juzipi.springbootinit.exception.BusinessException;
import com.juzipi.springbootinit.exception.ThrowUtils;
import com.juzipi.springbootinit.model.dto.question.QuestionVo;
import com.juzipi.springbootinit.model.dto.user.*;
import com.juzipi.springbootinit.model.entity.User;
import com.juzipi.springbootinit.model.vo.LoginUserVO;
import com.juzipi.springbootinit.model.vo.UserVO;
import com.juzipi.springbootinit.service.QuestiondataService;
import com.juzipi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.juzipi.springbootinit.service.impl.UserServiceImpl.SALT;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionDataController {

   @Resource
    private QuestiondataService questiondataService;

   @GetMapping("/list")
    public BaseResponse<List<QuestionVo>> questionList(){
       List<QuestionVo> questionVoList = questiondataService.questionList();
       return ResultUtils.success(questionVoList);
   }

}
