package com.maoyou.security.security.jwt;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * @ClassName JwtUser
 * @Description
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2022/7/20 16:06
 * @Version 1.0
 */
@Data
public class JwtUser implements UserDetails {
    private String password;

    private final String username;

    private final Set<GrantedAuthority> authorities;

    private final boolean accountNonExpired;

    private final boolean accountNonLocked;

    private final boolean credentialsNonExpired;

    private final boolean enabled;
}
