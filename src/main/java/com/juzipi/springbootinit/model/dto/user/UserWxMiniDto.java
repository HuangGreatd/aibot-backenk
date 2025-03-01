package com.juzipi.springbootinit.model.dto.user;

import lombok.Data;

/**
 * @ClassName UserWxMiniDto
 * @Description:
 * @Author: 橘子皮
 * @CreateDate: 2025/3/1 15:50
 */
@Data
public class UserWxMiniDto {
    private String session_key;
    private String openid;
}
