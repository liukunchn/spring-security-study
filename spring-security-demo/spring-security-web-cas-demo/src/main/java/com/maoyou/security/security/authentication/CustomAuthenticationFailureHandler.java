package com.maoyou.security.security.authentication;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 自定义登录失败处理
 */
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 小心url中文乱码啊！
        this.setDefaultFailureUrl("/login?error=" + URLEncoder.encode(exception.getMessage(), "utf-8"));
        super.onAuthenticationFailure(request, response, exception);
        System.out.println("认证失败");
    }
}
