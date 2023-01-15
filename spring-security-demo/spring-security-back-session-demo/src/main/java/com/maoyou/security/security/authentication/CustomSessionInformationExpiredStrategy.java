package com.maoyou.security.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maoyou.security.util.Response;
import com.maoyou.security.util.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName CustomSessionInformationExpiredStrategy
 * @Description
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2022/7/18 21:59
 * @Version 1.0
 */
public class CustomSessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {
    private final ObjectMapper objectMapper;

    public CustomSessionInformationExpiredStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        HttpServletResponse response = event.getResponse();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("Content-Type","text/plain;charset=utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(Response.error(ResponseCode.UNAUTHENTICATE).setMessage("您的账号在异地登录")));
        System.out.println("您的账号在异地登录");
    }
}
