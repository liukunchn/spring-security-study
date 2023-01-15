package com.maoyou.security.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maoyou.security.security.jwt.JwtRepository;
import com.maoyou.security.util.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义登录成功处理
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtRepository jwtRepository;

    private final ObjectMapper objectMapper;

    public CustomAuthenticationSuccessHandler(JwtRepository jwtRepository, ObjectMapper objectMapper) {
        this.jwtRepository = jwtRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 认证成功处理
     *  状态码返回200
     *  内容返回：0，成功，null
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Content-Type","text/plain;charset=utf-8");
        response.setHeader(jwtRepository.getJwtConfiguration().getTokenName(), jwtRepository.generateToken((UserDetails) authentication.getPrincipal()));
        response.getWriter().write(objectMapper.writeValueAsString(Response.success()));
        System.out.println("认证成功");
    }
}
