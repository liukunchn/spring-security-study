package com.maoyou.security.web;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName LoginController
 * @Description
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2022/7/1 0:29
 * @Version 1.0
 */
@Controller
public class LoginController {
    @RequestMapping({"/login"})
    public String login(Model model) {
        // 获取匿名用户，anoymousUser
        SecurityContext context = SecurityContextHolder.getContext();
        model.addAttribute("name", context.getAuthentication().getName());
        return "login";
    }
}
