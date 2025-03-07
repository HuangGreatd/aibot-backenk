package com.juzipi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzipi.springbootinit.mapper.ChatmessageMapper;
import com.juzipi.springbootinit.model.entity.Chatmessage;
import com.juzipi.springbootinit.service.ChatmessageService;
import org.springframework.stereotype.Service;

/**
* @author 73782
* @description 针对表【chatmessage(问题)】的数据库操作Service实现
* @createDate 2025-03-07 16:50:40
*/
@Service
public class ChatmessageServiceImpl extends ServiceImpl<ChatmessageMapper, Chatmessage>
    implements ChatmessageService{

}




