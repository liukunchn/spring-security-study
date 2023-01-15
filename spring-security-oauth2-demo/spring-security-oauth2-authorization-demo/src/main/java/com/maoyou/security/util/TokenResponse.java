package com.maoyou.security.util;

import lombok.Data;

/**
 * @ClassName TokenResponse
 * @Description
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2022/8/11 18:21
 * @Version 1.0
 */
@Data
public class TokenResponse {
    /**
     * {
     *     "access_token": "3tmUIsGphQxbW705vcoVutCqNw8=",
     *     "token_type": "bearer",
     *     "refresh_token": "p6LRj76TyLr5uf71pfy63+uOcyU=",
     *     "expires_in": 43199,
     *     "scope": "all"
     * }
     */
    private String access_token;
    private String token_type;
    private String refresh_token;
    private Long expires_in;
    private String scope;
    private String error;
    private String error_description;
}
