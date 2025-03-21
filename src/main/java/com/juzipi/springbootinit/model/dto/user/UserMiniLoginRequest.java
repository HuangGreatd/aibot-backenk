package com.juzipi.springbootinit.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 *

 */
@Data
public class UserMiniLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户头像
     */
    private String userAvatar;


    private String phone;


    private String code;


}
