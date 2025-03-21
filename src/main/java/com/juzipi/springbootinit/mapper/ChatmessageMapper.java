package com.juzipi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.juzipi.springbootinit.model.entity.Chatmessage;

import java.util.List;

/**
* @author 73782
* @description 针对表【chatmessage(问题)】的数据库操作Mapper
* @createDate 2025-03-07 16:50:40
* @Entity generator.domain.Chatmessage
*/
public interface ChatmessageMapper extends BaseMapper<Chatmessage> {

    List<Chatmessage> selectUserChatMessage(Long id);
}




