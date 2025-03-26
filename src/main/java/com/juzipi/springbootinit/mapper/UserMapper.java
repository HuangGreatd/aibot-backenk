package com.juzipi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.juzipi.springbootinit.model.entity.User;

/**
 * 用户数据库操作
 *

 */
public interface UserMapper extends BaseMapper<User> {

    User selectByPhone(String phone);


    User selectUserByOpenId(String openId);
}




