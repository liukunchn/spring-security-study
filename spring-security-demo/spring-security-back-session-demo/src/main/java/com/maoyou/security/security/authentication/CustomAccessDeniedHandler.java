package com.maoyou.security.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maoyou.security.util.Response;
import com.maoyou.security.util.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName CustomAccessDeniedHandler
 * @Description
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2022/7/18 22:31
 * @Version 1.0
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setHeader("Content-Type","text/plain;charset=utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(Response.error(ResponseCode.UNAUTHORIZED).setMessage("未授权")));
        System.out.println("未登录");
    }
}
