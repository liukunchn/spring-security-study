package com.maoyou.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maoyou.security.util.Response;
import com.maoyou.security.util.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 一旦标注了@EnableResourceServer，那么@EnableWebSecurity就没有作用了
 * 这个系统的认证功能将不再由@EnableResourceServer中配置的UsernamePasswordAuthenticationFilter处理，而是通过/check_token去授权服务器解析，或者直接通过jwt解析
 * 这个系统的授权功能也不再由@EnableResourceServer中配置的AuthorizationFilter处理，而是通过@EnableResourceServer中配置的HttpSecurity去处理，是否还是AuthorizationFilter?
 *
 * 授权码模式是一定需要一个客户端后端的，因为要用后端的接口作为回调地址，去获取token
 *
 * 资源服务器：发现未登录怎么处理？发现未登录肯定是在资源服务器发现的，可以通过.authenticationEntryPoint()来配置
 *      如果访问自己的资源服务器，应该保存ref信息用于最后的重定向，并告诉前端重定向到登录授权页，需要约定响应来配合
 *      如果第三方应用访问资源服务器，不用保存ref信息用于最后的重定向，并告诉前端重定向到登录授权页，需要约定响应来配合。其实也可以保存ref
 *          第三方访问除了后端接口方式，还有别的方式吗？是否存在可能前后端分离，token存在前端？
 *      总结一下：这里是可以返回统一的响应的！
 *          在微服务中，可以将网关作为资源服务器，未登录处理只在网关中处理即可，其他服务已经能够确保已登录。
 *          现在授权服务器已经出来了，那么授权服务器开箱即用，应该不是配置在网关中的。那未登录的配置也可以配置在各个服务中？
 *          知道了：未登录的配置是资源服务器来配置，就是可以配置在网关。而授权服务器开箱即用和这里没关系的
 *
 * 客户端回调地址：获取token之后怎么保存？获取了token相当于登录成功
 *      如果访问自己的资源服务器：
 *          这里一般是资源服务器自己作为客户端，或者微服务网关作为客户端
 *          应该把token存在前端，然后调用后端接口的时候带上token。并且这里应该返回一个重定向信息，重定向到之前的ref页面
 *      如果第三方应用访问资源服务器，一般是通过java来调用接口，这种的一般把token存在后端，然后调用的时候带上token
 *      这两种方式怎么统一？不需要统一，访问自己的是在网关来写，如果是第三方则写在第三方自己的代码里面
 *      总结一下就是：自己使用和第三方使用，保存的方式不一样
 *
 * 现在可以写两个demo，一个单体版本的，一个微服务版本的
 *      单体版本：授权服务器，资源服务器，资源服务器同时作为客户端，前后端分离应用
 *      TODO 微服务版本，授权服务器（新版开箱即用，可能和网关整合），资源服务器，网关作为客户端（也可能作为资源服务器去鉴权，而后面的资源服务器不用鉴权了？）
 *
 *
 *      问题：在微服务项目中，资源服务器只是web层的服务，还是所有的服务？
 *      问题：资源服务器在基于数据库的动态鉴权时，每次都去查询数据库，能否有什么解决办法？
 *          解决：这个项目作为资源服务器和客户端的所有功能都可以再微服务网关中完成，
 *              并且获取动态权限的时候可以增加缓存
 *              网关可以把认证和授权的信息，即是UsernamePasswordAuthentication放在请求头中
 *              后面的所有服务加一个过滤器，从请求头中获取凭证，然后放入SecurityContextHolder即可
 *              后面的所有服务是否还需要去鉴权。我感觉是不需要，都弄成登录即可访问就行了吧。如果动态鉴权又要去查数据库，交给网关就可以了。
 *
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    public static final String RESOURCE_ID = "res1";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FilterInvocationSecurityMetadataSource securityMetadataSource;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
                // 资源ID
                .resourceId(RESOURCE_ID)
                // token服务
//                .tokenServices(tokenService())
                // token存储，解析jwt
                .tokenStore(tokenStore())
                // 认证接入点，未登录
                .authenticationEntryPoint(((request, response, authException) -> {
                    // todo 保存前端的ref静态页面
                    // 重定向到/oauth/authorize
                    // http://127.0.0.1:8080/oauth/authorize?client_id=self&response_type=code&scope=all&redirect_uri=http://127.0.0.1:8081/callback
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setHeader("Content-Type","text/plain;charset=utf-8");
                    response.getWriter().write(objectMapper.writeValueAsString(Response.error(ResponseCode.UNAUTHENTICATE).setMessage("未登录")));
                }))
                ;
    }

//    @Bean
//    public ResourceServerTokenServices tokenService() {
//        //使用远程服务请求授权服务器校验token,必须指定校验token 的url、client_id，client_secret
//        RemoteTokenServices service=new RemoteTokenServices();
//        service.setCheckTokenEndpointUrl("http://127.0.0.1:8080/oauth/check_token");
//        service.setClientId("c1");
//        service.setClientSecret("secret");
//        return service;
//    }
    @Bean
    public TokenStore tokenStore(){  //Token存储方式现在改为JWT存储
        return new JwtTokenStore(tokenConverter());  //传入刚刚定义好的转换器
    }
//    // 使用对称加密
//    @Bean
//    public JwtAccessTokenConverter tokenConverter(){  //Token转换器，将其转换为JWT
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//        converter.setSigningKey("maoyou");   //这个是对称密钥，一会资源服务器那边也要指定为这个
//        return converter;
//    }
    // 使用非对称加密
    @Bean
    public JwtAccessTokenConverter tokenConverter(){  //Token转换器，将其转换为JWT
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setVerifierKey(getPubKey());   //这个是非对称密钥
        return converter;
    }

    private String getPubKey() {
        Resource resource = new ClassPathResource("pubkey.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            System.out.println("本地公钥");
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            return getKeyFromAuthorizationServer();
        }
    }

    private String getKeyFromAuthorizationServer() {
        ObjectMapper objectMapper = new ObjectMapper();
        String pubKey = new RestTemplate().getForObject("http://127.0.0.1:8080/oauth/token_key", String.class);
        try {
            Map map = objectMapper.readValue(pubKey, Map.class);
            System.out.println("联网公钥");
            return map.get("value").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .antMatchers("/callback").permitAll()
//                .antMatchers("/", "/index").hasRole("USER")
////                .antMatchers("/user").hasRole("ADMIN")
////                .mvcMatchers("/user").access("hasAuthority('SCOPE_all')")
//                .anyRequest().authenticated();
//    }
    // 基于数据的动态权限
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/callback").permitAll()
                .anyRequest().authenticated()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(
                            O fsi) {
                        fsi.setSecurityMetadataSource(securityMetadataSource);
                        fsi.setAccessDecisionManager(new AffirmativeBased(Arrays.asList(
//                              new WebExpressionVoter(),
                                new RoleVoter(),
                                new AuthenticatedVoter()
                        )));
                        return fsi;
                    }
                });
    }
}
