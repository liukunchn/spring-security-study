package com.maoyou.security.config;

import com.maoyou.security.security.authentication.CustomAuthenticationFailureHandler;
import com.maoyou.security.security.authentication.CustomAuthenticationSuccessHandler;
import com.maoyou.security.security.authentication.CustomLogoutSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import static org.springframework.security.config.Customizer.withDefaults;


/**
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // 认证失败和登出时，重定向到/login?error和/login?logout
                        .mvcMatchers("/login").permitAll()
                        // 静态资源过滤
                        .antMatchers("/imgs/**").permitAll()
                        .antMatchers("/js/**").permitAll()
                        .antMatchers("/css/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                                // 自定义登录页
                                .loginPage("/login").permitAll()    //如果配置了loginPage，那么必须自己实现一个/login的get请求，即便是/login也需要实现
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
                                .maximumSessions(10)
                                .maxSessionsPreventsLogin(true)
                )

                // csrfp配置
                // todo 开启csrf是否可能存在问题
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
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // 禁用csrf
//                        .disable()
                )

        // 异常处理
//                .exceptionHandling(exceptionHandling -> exceptionHandling
//                        // 访问拒绝，一般指的是InvalidCsrfTokenException？
//                        // 由于_csrf是保存在session中的，在session过期时访问，可能会抛出这个异常
//                        .accessDeniedHandler()
//                        // 登录入口，什么异常？
//                        .authenticationEntryPoint()
//                )
                ;
    }

    /**
     * 用于session并发控制
     * @return
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    // 不再使用写死的userdetailsservice，而使用security.core.CustomUserDetailsService来从数据库中动态加载用户和权限信息
//    @Bean
//    public UserDetailsService userDetailsService() {
//        // The builder will ensure the passwords are encoded before saving in memory
////        User.UserBuilder users = User.withDefaultPasswordEncoder();
//        User.UserBuilder users = User.builder();// 如果要使用bcrypt加密，那么使用builder()
//        UserDetails user = users
//                .username("user")
//                .password(new BCryptPasswordEncoder().encode("password"))
////                .password("password")
//                .roles("USER")
//                .build();
//        UserDetails admin = users
//                .username("admin")
//                .password(new BCryptPasswordEncoder().encode("password"))
//                .roles("USER", "ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(user, admin);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Override
//    @Bean
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }


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
        customLogoutSuccessHandler.setDefaultTargetUrl("/login?logout");
        return customLogoutSuccessHandler;
    }

}
