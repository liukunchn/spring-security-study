package com.maoyou.security.web;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName IndexController
 * @Description
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2022/6/30 14:40
 * @Version 1.0
 */
@Controller
public class IndexController {
    @RequestMapping({"/index","/"})
    public String index(Model model) {
        // 可以通过SecurityContextHolder获取SecurityContext，这是通过SecurityContextPersistenceFilter实现的
        SecurityContext context = SecurityContextHolder.getContext();
        model.addAttribute("username", ((User)context.getAuthentication().getPrincipal()).getUsername());
        model.addAttribute("name", context.getAuthentication().getName());
        return "index";
    }
}
