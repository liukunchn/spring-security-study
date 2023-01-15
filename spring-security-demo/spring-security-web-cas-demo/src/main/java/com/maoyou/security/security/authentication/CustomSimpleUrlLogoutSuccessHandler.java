package com.maoyou.security.security.authentication;

import org.springframework.security.core.Authentication;
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
public class CustomSimpleUrlLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    private final String service;

    public CustomSimpleUrlLogoutSuccessHandler(String service) {
        this.service = service;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        setDefaultTargetUrl("http://sso.maoyou.com:8080/cas/logout?service=http://sso.maoyou.com:8080/cas/login?service=" + URLEncoder.encode(service));
        super.onLogoutSuccess(request, response, authentication);
    }
}
