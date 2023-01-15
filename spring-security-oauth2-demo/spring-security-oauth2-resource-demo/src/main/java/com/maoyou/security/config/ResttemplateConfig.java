package com.maoyou.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName ResttemplateConfig
 * @Description
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2022/8/11 18:16
 * @Version 1.0
 */
@Configuration
public class ResttemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
