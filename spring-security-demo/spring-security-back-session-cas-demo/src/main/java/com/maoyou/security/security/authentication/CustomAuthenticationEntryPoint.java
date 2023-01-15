package com.maoyou.security.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maoyou.security.util.Response;
import com.maoyou.security.util.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义登录入口
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("Content-Type","text/plain;charset=utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(Response.error(ResponseCode.UNAUTHENTICATE).setMessage("未登录")));
        System.out.println("未登录");
    }
}
