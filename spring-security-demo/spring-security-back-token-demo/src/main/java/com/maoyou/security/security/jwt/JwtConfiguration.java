package com.maoyou.security.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName JwtConfiguration
 * @Description
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2022/7/20 10:38
 * @Version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.security.jwt")
public class JwtConfiguration {
    /**
     * 密匙
     */
    private String secret = "jwt";

    /**
     * 过期时间,单位毫秒，默认为一天
     */
    private Long expiration = 1000 * 60 * 60 * 24L;

    /**
     * token名称
     */
    private String tokenName = "Authorization";

    /**
     * token redis 前缀
     */
    private String tokenRedisPrefix = "JWT_TOKEN_";


}
