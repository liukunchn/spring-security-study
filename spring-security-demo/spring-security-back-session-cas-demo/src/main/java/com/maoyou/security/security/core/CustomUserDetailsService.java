package com.maoyou.security.security.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.maoyou.security.entity.Role;
import com.maoyou.security.entity.User;
import com.maoyou.security.entity.Userrole;
import com.maoyou.security.service.RoleService;
import com.maoyou.security.service.UserService;
import com.maoyou.security.service.UserroleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义用户详情服务
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserroleService userroleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户
        User user = userService.getOne(new QueryWrapper<User>().eq("USERNAME", username));
        if (user != null) {
            // 查询用户的权限
            List<GrantedAuthority> grantedAuthorities;
            List<Userrole> userroles = userroleService.list(new QueryWrapper<Userrole>().eq("USERID", user.getUserid()));
            if (CollectionUtils.isEmpty(userroles)) {
                grantedAuthorities = new ArrayList<>();
            } else {
                List<Role> roles = roleService.listByIds(userroles.stream().map(Userrole::getRoleid).collect(Collectors.toList()));
                grantedAuthorities = roles.stream().map(role -> {
                    Assert.isTrue(!role.getRolename().startsWith("ROLE_"),
                            () -> role.getRolename() + "不能以ROLE_开始（它是自动添加的）");
                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + role.getRolename());
                    return grantedAuthority;
                }).collect(Collectors.toList());
            }
            // 返回UserDetails对象
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getName())
                    .password(user.getPassword())
                    .credentialsExpired(System.currentTimeMillis() - user.getPwdlastmodifydate().getTime() > 30L * 24 * 60 * 60 * 1000)
                    .accountLocked("1".equals(user.getLocked()))
                    .disabled("1".equals(user.getDestory()))
                    // 配置密码编码器。应该在Spring容器中注册一个PasswordEncoder，否则报错
//                    .passwordEncoder(new BCryptPasswordEncoder()::encode)
                    // 配置动态的用户权限。可以使用roles()也可以使用authorities()，roles会自动添加ROLE_
//                    .roles()
                    .authorities(grantedAuthorities)
                    .build();
        }
        throw new UsernameNotFoundException("账户名或密码不正确");
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode("111111");
        System.out.println(encode);
        // $2a$10$I2WdXKJwUDRZv3LwJEqRdeCQ.T7XIng7tSC74.yqLGBNz2UyatCp2
    }
}
