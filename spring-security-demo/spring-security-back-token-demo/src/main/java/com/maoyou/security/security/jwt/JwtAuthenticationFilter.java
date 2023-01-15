package com.maoyou.security.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName JwtFilter
 * @Description Jwt过滤器
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2021/4/9 16:31
 * @Version 1.0
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtRepository jwtRepository;

    private final JwtConfiguration jwtConfiguration;

    public JwtAuthenticationFilter(JwtRepository jwtRepository, JwtConfiguration jwtConfiguration) {
        this.jwtRepository = jwtRepository;
        this.jwtConfiguration = jwtConfiguration;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(jwtConfiguration.getTokenName());
        if (!StringUtils.hasLength(token)) {
            token = request.getParameter(jwtConfiguration.getTokenName());
        }
        if (!StringUtils.hasLength(token)) {
            filterChain.doFilter(request, response);
        } else {
            UserDetails userDetails = jwtRepository.validateToken(token);
            if (userDetails == null) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
            } else {
                UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                jwtRepository.refreshToken(token);
                filterChain.doFilter(request, response);
            }
        }
    }

}
