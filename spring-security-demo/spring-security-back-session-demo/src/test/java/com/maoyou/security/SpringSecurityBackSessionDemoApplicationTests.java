package com.maoyou.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringSecurityBackSessionDemoApplicationTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
        System.out.println(objectMapper);
    }

}
