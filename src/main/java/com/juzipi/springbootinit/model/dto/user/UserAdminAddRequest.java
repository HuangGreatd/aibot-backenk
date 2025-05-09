package com.juzipi.springbootinit.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户创建请求
 */
@Data
public class UserAdminAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;


    /**
     * 用户角色: user, admin
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}