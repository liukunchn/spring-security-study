package com.maoyou.security.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maoyou.security.util.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @ClassName CustomSimpleUrlLogoutSuccessHandler
 * @Description
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2022/7/31 0:02
 * @Version 1.0
 */
public class CustomSimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
    private final String service;
    private final ObjectMapper objectMapper;

    public CustomSimpleUrlLogoutSuccessHandler(String service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String redirectUrl = "http://sso.maoyou.com:8080/cas/logout?service=http://sso.maoyou.com:8080/cas/login?service=" + URLEncoder.encode(service);
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Content-Type","text/plain;charset=utf-8");
        Response<Object> res = Response.success();
        res.setRedirectUrl(redirectUrl);
        response.getWriter().write(objectMapper.writeValueAsString(res));
        System.out.println("单点登出成功");
    }
}
