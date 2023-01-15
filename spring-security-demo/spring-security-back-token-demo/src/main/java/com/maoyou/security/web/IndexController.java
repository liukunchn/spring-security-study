package com.maoyou.security.web;

import com.maoyou.security.util.Response;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName IndexController
 * @Description
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2022/6/30 14:40
 * @Version 1.0
 */
@RestController
public class IndexController {
    @RequestMapping({"/index","/"})
    public Response index() {
        Model model = new ExtendedModelMap();
        // 可以通过SecurityContextHolder获取SecurityContext，这是通过SecurityContextPersistenceFilter实现的
        SecurityContext context = SecurityContextHolder.getContext();
        model.addAttribute("name", context.getAuthentication().getName());
        return Response.success(model);
    }

    @RequestMapping({"/user"})
    public Response user() {
        Model model = new ExtendedModelMap();
        // 可以通过SecurityContextHolder获取SecurityContext，这是通过SecurityContextPersistenceFilter实现的
        SecurityContext context = SecurityContextHolder.getContext();
        model.addAttribute("name", context.getAuthentication().getName());
        return Response.success(model);
    }
}
