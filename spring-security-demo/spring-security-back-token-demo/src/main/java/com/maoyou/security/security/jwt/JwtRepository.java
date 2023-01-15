package com.maoyou.security.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName JwtTokenUtil
 * @Description 生成令牌，验证等等一些操作
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2021/4/8 16:02
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix = "maoyou.jwt")
@Component
@Slf4j
public class JwtRepository {
    @Autowired
    private JwtConfiguration jwtConfiguration;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 生成令牌
     *
     * @param userDetails 用户
     * @return 令牌
     */
    public String generateToken(UserDetails userDetails) throws JsonProcessingException {
        Date now = new Date();
//        Date expirationDate = new Date(now.getTime() + expiration);
        // 有效荷载载荷
        // 标准中注册的声明（建议但不强制使用）
        Claims claims = new DefaultClaims();
        claims.setIssuer("server");
        claims.setSubject(userDetails.getUsername());
        claims.setAudience("client");
//        claims.setExpiration(expirationDate);  // 如果不使用redis，需要设置token的过期时间
        claims.setNotBefore(now);
        claims.setIssuedAt(now);
        claims.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        claims.put("authorities", userDetails.getAuthorities());
        // 公共的声明
        // 私有的声明
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS512, jwtConfiguration.getSecret()).setClaims(claims).compact();
        redisTemplate.opsForValue().set(jwtConfiguration.getTokenRedisPrefix() + token, objectMapper.writeValueAsString(userDetails), jwtConfiguration.getExpiration(), TimeUnit.MILLISECONDS);
        return token;
    }

//    public static void main(String[] args) {
//        Date now = new Date();
////        Date expirationDate = new Date(now.getTime() + expiration);
//        // 有效荷载载荷
//        // 标准中注册的声明（建议但不强制使用）
//        Claims claims = new DefaultClaims();
//        claims.setIssuer("server");
//        claims.setSubject("zhangsan");
//        claims.setAudience("client");
////        claims.setExpiration(expirationDate);  // 如果不使用redis，需要设置token的过期时间
//        claims.setNotBefore(now);
//        claims.setIssuedAt(now);
//        claims.setId(UUID.randomUUID().toString().replaceAll("-", ""));
////        claims.put("authorities", userDetails.getAuthorities());
//        // 公共的声明
//        // 私有的声明
//        String token = Jwts.builder().signWith(SignatureAlgorithm.HS512, "maoyou").setClaims(claims).compact();
//    }

    /**
     * 验证令牌
     * @param token       令牌
     * @return UserDetails          用户
     */
    public UserDetails validateToken(String token) {
        // token为空校验
        if (!StringUtils.hasText(token)) {
            return null;
        }

        // jwt校验
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(jwtConfiguration.getSecret()).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage());
            }
            return null;
        }
        String userDetailsJson = redisTemplate.opsForValue().get(jwtConfiguration.getTokenRedisPrefix() + token);
        if (!StringUtils.hasText(userDetailsJson)) {
            return null;
        }

        // redis校验
        JwtUser user;
        try {
            user = objectMapper.readValue(userDetailsJson, JwtUser.class);
        } catch (JsonProcessingException e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage());
            }
            return null;
        }
        return user;
    }

    /**
     * 刷新令牌（使用redis）
     * @param token
     * @return
     */
    public String refreshToken(String token) {
        redisTemplate.expire(jwtConfiguration.getTokenRedisPrefix() + token,jwtConfiguration.getExpiration(), TimeUnit.MILLISECONDS);
        return token;
    }

    public void clearToken(String token) {
        redisTemplate.delete(jwtConfiguration.getTokenRedisPrefix() + token);
    }

}
