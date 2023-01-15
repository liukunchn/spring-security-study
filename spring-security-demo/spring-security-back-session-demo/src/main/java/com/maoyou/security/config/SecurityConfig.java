package com.maoyou.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maoyou.security.security.authentication.*;
import com.maoyou.security.security.authorization.AccessDecisionManagerAuthorizationManagerAdapter;
import com.maoyou.security.security.authorization.AccessDecisionVoterAuthorizationManagerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

/**
 *
 */
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AccessDecisionVoterAuthorizationManagerAdapter accessDecisionVoterAuthorizationManagerAdapter;
    @Autowired
    private AccessDecisionManagerAuthorizationManagerAdapter accessDecisionManagerAuthorizationManagerAdapter;

    /**
     * 配置filterChain
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        // 默认配置
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(withDefaults())
//                .formLogin(withDefaults())
//                .logout(withDefaults());
//        return http.build();

        // 自定义配置
        http
                // 配置权限
                .authorizeHttpRequests(authorize -> authorize
                        // 认证失败和登出时，重定向到/login?error和/login?logout
                        .mvcMatchers("/login").permitAll()
                        // 静态资源过滤
                        .antMatchers("/imgs/**").permitAll()
                        .antMatchers("/js/**").permitAll()
                        .antMatchers("/css/**").permitAll()
                        // 其他请求都需要认证
//                        .anyRequest().authenticated()
                        // 其他请求改为自定义的认证方式
                        // 访问决策投票器
//                        .anyRequest().access(accessDecisionVoterAuthorizationManagerAdapter)
                        // 访问决策管理器
                        .anyRequest().access(accessDecisionManagerAuthorizationManagerAdapter)
                )

                // 配置UsernamePasswordAuthenticationFilter
                .formLogin(form -> form
                        // 自定义登录页
//                        .loginPage("/login").permitAll()    //如果配置了loginPage，那么必须自己实现一个/login的get请求，即便是/login也需要实现
//                        .loginProcessingUrl("login")    //如果配置了loginProcessingUrl，如果是/login则不需要实现一个/login的post请求
                        // 自定义登录成功处理
//                        .defaultSuccessUrl("/", false)
                        .successHandler(customAuthenticationSuccessHandler())
                        // 自定义登录失败处理
                        // 【这里有个问题：/login?error会重定向到/login，那么必须将login配置成无需登录即可访问】
//                        .failureUrl("/login?error")
                        .failureHandler(customAuthenticationFailureHandler())
                )

                // 配置LogoutFilter
                .logout(logout -> logout
                        // 自定义登出url
//                        .logoutUrl("/logout")   //如果配置了logoutUrl，如果是/logout则不需要实现一个/logout的post请求
                        // 自定义登出成功处理
                        // 【这里有个问题：/login?error会重定向到/login，那么必须将login配置成无需登录即可访问】
//                        .logoutSuccessUrl("/login?logout").permitAll()
                        .logoutSuccessHandler(customLogoutSuccessHandler())
                        // 自定义登出处理
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )

                // 配置session行为，ForceEagerSessionCreationFilter，ConcurrentSessionFilter，SessionManagementFilter
                .sessionManagement(session -> session
                        // ???
//                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        // ???
//                        .invalidSessionUrl("/invalidSession.htm")
                        // session并发控制
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredSessionStrategy(sessionInformationExpiredStrategy())
                )

                // csrf配置
                .csrf(csrf -> csrf
                        // 设置csrfTokenRepository为cookie
                        // 添加之后会设置一个cookie：XSRF-TOKEN=e35243f6-52e8-490d-8454-d4324ba695cc
                        // 添加之后，session中依然需要保存_csrf.headerName=_csrf?,_csrf.token
                        /**
                         * 如果要使用ajax的方式包含csrf
                         *   this.$http.post(url, data, {
                         *       headers: {
                         *           'http请求头信息字段名': 'cookie中的token'
                         *       }
                         *   }).then((res) => {})
                         */
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // 禁用csrf
                        .disable()
                )

                 // 异常处理
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        // 如果没有权限，回抛出
                        // 由于_csrf是保存在session中的，在session过期时访问，会抛出InvalidCsrfTokenException
                        .accessDeniedHandler(accessDeniedHandler())
                        // 登录入口，如果未登录，会抛出AuthenticationException
                        .authenticationEntryPoint(entryPoint())
                )
                ;
        return http.build();
    }



    /**
     * 用于session并发控制
     * @return
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }



    /**
     * 配置userDetailService
     * 基于内存的用户详情管理器
     * @return
     */
//    @Bean
//    public UserDetailsService userDetailsService() {
//        // The builder will ensure the passwords are encoded before saving in memory
//        User.UserBuilder users = User.withDefaultPasswordEncoder();
//        UserDetails user = users
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//        UserDetails admin = users
//                .username("admin")
//                .password("password")
//                .roles("USER", "ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(user, admin);
//    }

    /**
     * 配置userDetailService
     * 基于自定义的用户详情管理器
     * 注意：在CustomUserDetailsService上使用了@Component注解，所以这里直接注释掉
     * @return
     */
//    @Bean
//    public UserDetailsService userDetailsService() {
//        CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService();
//        return customUserDetailsService;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 认证成功处理
     * @return
     */
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler = new CustomAuthenticationSuccessHandler(objectMapper);
        return customAuthenticationSuccessHandler;
    }

    /**
     * 认证失败处理
     * @return
     */
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        CustomAuthenticationFailureHandler customAuthenticationFailureHandler = new CustomAuthenticationFailureHandler(objectMapper);
        return customAuthenticationFailureHandler;
    }

    /**
     * 登出成功处理
     * @return
     */
    public LogoutSuccessHandler customLogoutSuccessHandler() {
        CustomLogoutSuccessHandler customLogoutSuccessHandler = new CustomLogoutSuccessHandler(objectMapper);
        return customLogoutSuccessHandler;
    }

    /**
     * 未登录，登录入口。这里直接返回一个401状态
     * @return
     */
    public AuthenticationEntryPoint entryPoint() {
        return new CustomAuthenticationEntryPoint(objectMapper);
    }

    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler(objectMapper);
    }

    /**
     * session过期策略
     * @return
     */
    public SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
        return new CustomSessionInformationExpiredStrategy(objectMapper);
    }

}
