package com.juzipi.springbootinit.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static com.juzipi.springbootinit.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @ClassName WsHandshakeInterceptor
 * @Description: WebSocket 拦截器，建立连接前要先校验
 * @Author: 橘子皮
 * @CreateDate: 2025/2/21 19:49
 */
@Slf4j
@Component
public class WsHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        //获取请求头中的自定义信息
        String tokenName = request.getHeaders().getFirst("tokenName");
        String tokenValue = request.getHeaders().getFirst("tokenValue");
        log.info("tokenName: {}, tokenValue: {}", tokenName, tokenValue);
        // 将 tokenValue 存入 attributes 中
        attributes.put("tokenValue", tokenValue);
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            attributes.put("request", servletRequest.getServletRequest());
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
