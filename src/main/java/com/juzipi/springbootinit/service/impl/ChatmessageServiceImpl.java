package com.juzipi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzipi.springbootinit.mapper.ChatmessageMapper;
import com.juzipi.springbootinit.model.entity.Chatmessage;
import com.juzipi.springbootinit.model.vo.MessageVO;
import com.juzipi.springbootinit.service.ChatmessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 73782
 * @description 针对表【chatmessage(问题)】的数据库操作Service实现
 * @createDate 2025-03-07 16:50:40
 */
@Service
public class ChatmessageServiceImpl extends ServiceImpl<ChatmessageMapper, Chatmessage>
        implements ChatmessageService {

    @Resource
    private ChatmessageMapper chatmessageMapper;

    @Override
    public List<MessageVO> selectUserChatMessage(Long id) {
        List<Chatmessage> chatmessageList = chatmessageMapper.selectUserChatMessage(id);
        return chatmessageList.stream().map(chatmessage -> {
            MessageVO messageVO = new MessageVO();
            messageVO.setId(chatmessage.getId());
            messageVO.setFromMessage(chatmessage.getFromMessage());
            messageVO.setToMessage(chatmessage.getToMessage());
            messageVO.setCreateTime(chatmessage.getCreateTime());
            return messageVO;
        }).collect(Collectors.toList());
    }

    @Override
    public void inserChatMessage(Long userId,String fromMessage,String toMessage){


    }
}




