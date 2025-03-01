package com.juzipi.springbootinit.constant;

/**
 * 用户常量
 */
public interface UserConstant {
    /**
     * 微信 auth.code2Session 接口
     */
    final String WECHAT_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";

    // endregion
}
