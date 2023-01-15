package com.maoyou.security.config;

import com.maoyou.security.security.authentication.CustomAuthenticationFailureHandler;
import com.maoyou.security.security.authentication.CustomAuthenticationSuccessHandler;
import com.maoyou.security.security.authentication.CustomLogoutSuccessHandler;
import com.maoyou.security.security.authentication.CustomSimpleUrlLogoutSuccessHandler;
import com.maoyou.security.security.authorization.AccessDecisionManagerAuthorizationManagerAdapter;
import com.maoyou.security.security.authorization.AccessDecisionVoterAuthorizationManagerAdapter;
import com.maoyou.security.security.core.CustomUserDetailsService;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 *
 */
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private AccessDecisionVoterAuthorizationManagerAdapter accessDecisionVoterAuthorizationManagerAdapter;
    @Autowired
    private AccessDecisionManagerAuthorizationManagerAdapter accessDecisionManagerAuthorizationManagerAdapter;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

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
                // 添加cas认证过滤器
                .addFilter(casAuthenticationFilter())
                // 添加单点退出过滤器（单点服务器退出时广播会走这个过滤器）
                .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
                // 添加单点退出请求过滤器（客户端退出先走/logout，然后重定向到/logout/cas，也就是走这个过滤器）
                .addFilterBefore(requestSingleLogoutFilter(), LogoutFilter.class)
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

//                // 配置UsernamePasswordAuthenticationFilter
//                .formLogin(form -> form
//                        // 自定义登录页
//                        .loginPage("/login").permitAll()    //如果配置了loginPage，那么必须自己实现一个/login的get请求，即便是/login也需要实现
////                        .loginProcessingUrl("login")    //如果配置了loginProcessingUrl，如果是/login则不需要实现一个/login的post请求
//                        // 自定义登录成功处理
////                        .defaultSuccessUrl("/", false)
//                        .successHandler(customAuthenticationSuccessHandler())
//                        // 自定义登录失败处理
//                        // 【这里有个问题：/login?error会重定向到/login，那么必须将login配置成无需登录即可访问】
////                        .failureUrl("/login?error")
//                        .failureHandler(customAuthenticationFailureHandler())
//                )

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
                        .maximumSessions(5)
                        .maxSessionsPreventsLogin(true)
                )

                // csrfp配置
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
                        // 访问拒绝，一般指的是InvalidCsrfTokenException？
                        // 由于_csrf是保存在session中的，在session过期时访问，可能会抛出这个异常
//                        .accessDeniedHandler()
                        // 登录入口，什么异常？
                        .authenticationEntryPoint(casAuthenticationEntryPoint())
                )
                ;
        return http.build();
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService("http://client1.maoyou.com:9080/login/cas");
        return serviceProperties;
    }

    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLoginUrl("http://sso.maoyou.com:8080/cas/login");
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        return casAuthenticationEntryPoint;
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        casAuthenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler());
//        casAuthenticationFilter.setAuthenticationFailureHandler();
        return casAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        ProviderManager providerManager = new ProviderManager(casAuthenticationProvider());
        return providerManager;
    }

    /**
     * @return
     */
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        UserDetailsByNameServiceWrapper userDetailsByNameServiceWrapper = new UserDetailsByNameServiceWrapper(customUserDetailsService);
        casAuthenticationProvider.setAuthenticationUserDetailsService(userDetailsByNameServiceWrapper);
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        Cas30ServiceTicketValidator cas30ServiceTicketValidator = new Cas30ServiceTicketValidator("http://sso.maoyou.com:8080/cas");
        casAuthenticationProvider.setTicketValidator(cas30ServiceTicketValidator);
        casAuthenticationProvider.setKey("an_id_for_this_auth_provider_only");
        return casAuthenticationProvider;
    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        return new SingleSignOutFilter();
    }

    @Bean
    public LogoutFilter requestSingleLogoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(customSimpleUrlLogoutSuccessHandler(), new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl("/logout/cas");
        return logoutFilter;
    }

    @Bean
    public CustomSimpleUrlLogoutSuccessHandler customSimpleUrlLogoutSuccessHandler() {
        ServiceProperties serviceProperties = serviceProperties();
        String service = serviceProperties.getService();
        CustomSimpleUrlLogoutSuccessHandler customSimpleUrlLogoutSuccessHandler = new CustomSimpleUrlLogoutSuccessHandler(service);
        return customSimpleUrlLogoutSuccessHandler;
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
        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler = new CustomAuthenticationSuccessHandler();
        customAuthenticationSuccessHandler.setAlwaysUseDefaultTargetUrl(false);
        customAuthenticationSuccessHandler.setDefaultTargetUrl("/");
        return customAuthenticationSuccessHandler;
    }

    /**
     * 认证失败处理
     * @return
     */
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        CustomAuthenticationFailureHandler customAuthenticationFailureHandler = new CustomAuthenticationFailureHandler();
        customAuthenticationFailureHandler.setDefaultFailureUrl("/login?error");
        return customAuthenticationFailureHandler;
    }

    /**
     * 登出成功处理
     * @return
     */
    public LogoutSuccessHandler customLogoutSuccessHandler() {
        CustomLogoutSuccessHandler customLogoutSuccessHandler = new CustomLogoutSuccessHandler();
        customLogoutSuccessHandler.setDefaultTargetUrl("/logout/cas");
        return customLogoutSuccessHandler;
    }

}
