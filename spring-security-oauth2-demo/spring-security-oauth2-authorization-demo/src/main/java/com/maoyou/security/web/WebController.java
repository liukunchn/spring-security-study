package com.maoyou.security.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName WebController
 * @Description
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2022/8/4 10:56
 * @Version 1.0
 */
@RestController
public class WebController {
    @RequestMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @RequestMapping("/user")
    public String user() {
        return "user";
    }
}
