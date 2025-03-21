package com.juzipi.springbootinit.controller;

import com.juzipi.springbootinit.annotation.AuthCheck;
import com.juzipi.springbootinit.common.BaseResponse;
import com.juzipi.springbootinit.common.ErrorCode;
import com.juzipi.springbootinit.common.ResultUtils;
import com.juzipi.springbootinit.constant.UserConstant;
import com.juzipi.springbootinit.model.dto.question.QuestionVo;
import com.juzipi.springbootinit.model.entity.Chatmessage;
import com.juzipi.springbootinit.model.entity.User;
import com.juzipi.springbootinit.model.vo.MessageVO;
import com.juzipi.springbootinit.service.ChatmessageService;
import com.juzipi.springbootinit.service.QuestiondataService;
import com.juzipi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatMessageController {

    @Resource
    private ChatmessageService chatMessageService;

    @Resource
    private UserService userService;

    /**
     * 查询所有消息记录
     */
    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<Chatmessage>> chatListMessage() {
        List<Chatmessage> list = chatMessageService.list();
        return ResultUtils.success(list);
    }


    /**
     *
     */
    @GetMapping("/get")
    public BaseResponse<List<MessageVO>> getUserChatMessage(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
        Long id = loginUser.getId();
        List<MessageVO> chatmessageList = chatMessageService.selectUserChatMessage(id);
        return ResultUtils.success(chatmessageList);
    }
}
