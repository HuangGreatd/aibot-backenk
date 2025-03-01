package com.juzipi.springbootinit.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;

/**
 * @ClassName JwtUtil
 * @Description:
 * @Author: 橘子皮
 * @CreateDate: 2025/3/1 16:07
 */
public class JwtUtil {
    private static String JWT_SECRET_KEY = "HOPE_MP";

    // 接收业务数据,生成token 并返回
    public static String genToken(Map<String, Object> claims) {
        return JWT.create().withClaim("claims", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12)).sign(Algorithm.HMAC256(JWT_SECRET_KEY));
    }

    // 接收token,验证token,并返回业务数据
    public static Map<String, Object> parseToken(String token) {
        Map<String, Object> claims =
                JWT.require(Algorithm.HMAC256(JWT_SECRET_KEY)).build().verify(token).getClaim("claims").asMap();
        // 对claims进行遍历，将整数转换为长整数
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            if (entry.getValue() instanceof Integer) {
                entry.setValue(((Integer) entry.getValue()).longValue());
            }
        }
        return claims;
    }
}
