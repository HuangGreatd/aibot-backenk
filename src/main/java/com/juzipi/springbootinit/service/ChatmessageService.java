package com.juzipi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.juzipi.springbootinit.model.entity.Chatmessage;
import com.juzipi.springbootinit.model.vo.MessageVO;

import java.util.List;

/**
* @author 73782
* @description 针对表【chatmessage(问题)】的数据库操作Service
* @createDate 2025-03-07 16:50:40
*/
public interface ChatmessageService extends IService<Chatmessage> {

    List<MessageVO> selectUserChatMessage(Long id);
}
