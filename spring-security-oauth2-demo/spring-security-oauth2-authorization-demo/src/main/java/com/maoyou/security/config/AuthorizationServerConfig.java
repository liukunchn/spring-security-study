package com.maoyou.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.Arrays;

/**
 *  授权码流程
 *      1.获取授权码（需要认证用户，认证客户端）
 *          由资源服务器重定向
 *          http://localhost:8080/oauth/authorize?client_id=c1&response_type=code&scope=all&redirect_uri=http://www.baidu.com
 *
 *      2.获取token
 *          在回调地址中处理
 *          http://localhost:8080/oauth/token?grant_type=authorization_code&client_id=c1&client_secret=secret&code=xxx&redirect_uri=http://www.baidu.com
 *
 *      3.校验token。
 *          http://localhost:8080/oauth/check_token?token=
 *          如果使用jwt，那么这一步实际上不是必须的。但是有个获取公钥的接口：
 *          http://127.0.0.1:8080/oauth/token_key
 *
 *      4.访问资源服务器
 *          http://localhost:8081/user
 *
 *      授权服务器能否用@EnableResourceServer来控制资源？
 *          应该可以，如果用这个控制，那么还要写一套登录逻辑
 *          但是没有必要，授权服务器应该就只是处理认证和授权，如果有相关的业务，那可以写在另外一个资源服务器中
 *      作为第一方的客户端，能否使用密码模式？或者说是否应该使用密码模式？
 *          肯定是可以用密码模式的
 *          但是没有必要，密码模式是将账号密码放在/oauth/token的参数中，需要客户端自己获取账号密码，而不是去授权服务器的登录页认证
 *      授权服务器使用数据库存储client，userdetals以及权限
 *          数据库存储client，是用withClientDetails()，传入一个JdbcClientDetailsService
 *          数据库存储userDetails以及权限，是注入一个CustomUserDetailsService
 *      资源服务器使用数据库存储url需要的权限
 *          这里需要在资源服务器中进行鉴权相关的配置
 *          如果使用@EnableResourceServer注解，那么好像不能使用AuthorizationFilter，而是使用FilterSecurityInterceptor
 *          所以只能使用老板的配置，使用withObjectPostProcessor对而是使用FilterSecurityInterceptor进行后置处理
 *          给FilterSecurityInterceptor设置securityMetadataSource和AccessDecisionManager
 *      使用jwt,不可逆加密？
 *          tokenConverter使用setKeyPair()，KeyPair是从类路径加载maoyou.jks文件，其中包括了私钥和公钥
 *          私钥用来签名，公钥用来解签。资源服务器通过/oauth/token_key从授权服务器获取公钥
 *          资源服务器使用converter.setVerifierKey()而不再使用setSigningKey()。setSigningKey()的参数是从授权服务器获取的公钥，用来解签。
 *      spring security resource
 *      spring security client
 *      spring authorization server
 *
 *      todo 现在还有一个bug，token校验有时成功，有时失败？
 *
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

//    @Autowired
//    private ClientDetailsService clientDetailsService;
////
////    @Autowired
////    private AuthorizationCodeServices authorizationCodeServices;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JwtAccessTokenConverter accessTokenConverter;
//
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        clients
                // 使用数据库存储clientDetails
                // 第一种方式：使用withClientDetails()
                .withClientDetails(jdbcClientDetailsService())
                // 第二种方式：使用jdbc(dataSource)。使用这种方式好像存在问题，/check_token无法获取到信息,还没有找到原因
//                .jdbc(dataSource)
//                .passwordEncoder(passwordEncoder)
                // 使用内存存储clientDetails
//        clients.inMemory()// 使用in-memory存储
//                .withClient("c1")// client_id
//                .secret(new BCryptPasswordEncoder().encode("secret"))//客户端密钥。oauth2要求必须有一个PasswordEncoder,所以使用加密方式。
//                .resourceIds("res1")//资源列表
//                .authorizedGrantTypes("authorization_code", "password","client_credentials","implicit","refresh_token")// 该client允许的授权类型authorization_code,password,refresh_token,implicit,client_credentials
//                .scopes("all")// 允许的授权范围
//                .autoApprove(false)//false跳转到授权页面
//                .redirectUris("http://www.baidu.com")// 加上验证回调地址
//
//                .and()
//
//                .withClient("self")// client_id
//                .secret(new BCryptPasswordEncoder().encode("secret"))//客户端密钥。oauth2要求必须有一个PasswordEncoder,所以使用加密方式。
//                .resourceIds("res1")//资源列表
//                .authorizedGrantTypes("authorization_code", "password","client_credentials","implicit","refresh_token")// 该client允许的授权类型authorization_code,password,refresh_token,implicit,client_credentials
//                .scopes("all")// 允许的授权范围
//                .autoApprove(true)//false跳转到授权页面
//                .redirectUris("http://127.0.0.1:8081/callback")//加上验证回调地址
                ;
    }
    @Bean
    public ClientDetailsService jdbcClientDetailsService() {
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
       jdbcClientDetailsService.setPasswordEncoder(passwordEncoder);
        return jdbcClientDetailsService;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security){
        security
                .tokenKeyAccess("permitAll()")                    // oauth/token_key   若授权服务器是 JWT 非对称加密，则需要请求授权服务器的 /oauth/token_key 来获取公钥 key 进行解码
//                .checkTokenAccess("isAuthenticated()")                  // oauth/check_token todo 如果改成需要认证，那么要怎么认证？
                .checkTokenAccess("permitAll()")                  // oauth/check_token
                .allowFormAuthenticationForClients()				// oauth/token 获取token
        ;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .tokenServices(tokenService())//令牌管理服务
//                .tokenStore(tokenStore)// 可以使用tokenServices()，也可以使用tokenStore()
//                .setClientDetailsService(clientDetailsService)// 这里不配置也可以
//                .allowedTokenEndpointRequestMethods(HttpMethod.POST)// 这里也是不配置也可以
                ;
    }

    //令牌管理服务
    @Bean
    public AuthorizationServerTokenServices tokenService() {
        DefaultTokenServices service=new DefaultTokenServices();
        // token存储方式
        service.setTokenStore(tokenStore());//令牌存储策略
        // 是否支持刷新token
        service.setSupportRefreshToken(true);
        //令牌增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenConverter()));
        service.setTokenEnhancer(tokenEnhancerChain);
        service.setAccessTokenValiditySeconds(60 * 60 * 12); // 令牌默认有效期2小时
        service.setRefreshTokenValiditySeconds(60 * 60 * 24 * 30); // 刷新令牌默认有效期3天
        return service;
    }

    //配置token存储
//    @Bean
//    public TokenStore tokenStore() {
//        //使用内存存储令牌（普通令牌）
//        return new InMemoryTokenStore();
//    }
    // 使用jwt
    @Bean
    public TokenStore tokenStore(){  //Token存储方式现在改为JWT存储
        return new JwtTokenStore(tokenConverter());  //传入刚刚定义好的转换器
    }
//    // 使用HS256加密算法
//    @Bean
//    public JwtAccessTokenConverter tokenConverter(){  //Token转换器，将其转换为JWT
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//        converter.setSigningKey("maoyou");   //这个是对称密钥，一会资源服务器那边也要指定为这个
//        return converter;
//    }
    // 使用RSA加密算法
    @Bean
    public JwtAccessTokenConverter tokenConverter(){  //Token转换器，将其转换为JWT
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair());   //这个是对称密钥，一会资源服务器那边也要指定为这个
        return converter;
    }

    /**
     * 密钥对
     * JDK-keytool 生成证书命令参考：
     *  keytool -genkey -alias maoyou -keypass maoyou -keyalg RSA -storetype PKCS12 -keysize 1024 -validity 365 -keystore C:/Users/maoyou/Desktop/maoyou.jks -storepass maoyou -dname "CN=(Felord), OU=(felordcn), O=(felordcn), L=(zz), ST=(hn), C=(cn)"
     *  keytool -genkey -keyalg RSA -alias oh -keypass 212003 -storepass 456123 -keystore P:\git\rsa.jws
     * @return
     */
    @Bean
    public KeyPair keyPair() {
        // rsa 密钥文件, 以及对应的 storePass, keyPass, alias
        final ClassPathResource rsaJksRes = new ClassPathResource("maoyou.jks");
        final String storePass = "maoyou";
        final String keyPass = "maoyou";
        final String alias = "maoyou";
        return new KeyStoreKeyFactory(rsaJksRes, storePass.toCharArray()).getKeyPair(alias, keyPass.toCharArray());
    }



    //配置授权码服务
    //设置授权码模式的授权码如何存取，暂时采用内存方式
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }
//    // 将授权码模式服务的相关数据存储在数据库中
//    @Bean
//    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
//        return new JdbcAuthorizationCodeServices(dataSource);//设置授权码模式的授权码如何存取
//    }

}
