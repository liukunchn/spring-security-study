package com.maoyou.security.security.authorization;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.maoyou.security.entity.*;
import com.maoyou.security.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName CustomSecurityMetadataSource
 * @Description
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2022/7/8 15:25
 * @Version 1.0
 */
@Component
public class CustomSecurityMetadataSource implements SecurityMetadataSource {
    @Autowired
    private UrlService urlService;
    @Autowired
    private ResourceurlService resourceurlService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private RoleresourceService roleresourceService;
    @Autowired
    private RoleService roleService;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        RequestAuthorizationContext context = (RequestAuthorizationContext) object;
        HttpServletRequest request = context.getRequest();
        List<Url> urls = urlService.list();
        List<Url> matchedUrls = urls.stream().filter(url -> {
            RequestMatcher matcher = new AntPathRequestMatcher(url.getAddress());
            return matcher.matcher(request).isMatch();
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchedUrls)) {
            return buildAuthenticatedConfigAttribute();
        }
        List<Resourceurl> resourceurls = resourceurlService.list(new QueryWrapper<Resourceurl>().in("URLID", matchedUrls.stream().map(url -> url.getId()).collect(Collectors.toList())));
        if (CollectionUtils.isEmpty(resourceurls)) {
            return buildAuthenticatedConfigAttribute();
        }
        List<Resource> resources = resourceService.list(new QueryWrapper<Resource>().in("RESOURCEID", resourceurls.stream().map(resourceurl -> resourceurl.getResourceid()).collect(Collectors.toList())));
        if (CollectionUtils.isEmpty(resources)) {
            return buildAuthenticatedConfigAttribute();
        }
        List<Roleresource> roleresources = roleresourceService.list(new QueryWrapper<Roleresource>().in("RESOURCEID", resources.stream().map(resource -> resource.getResourceid()).collect(Collectors.toList())));
        if (CollectionUtils.isEmpty(roleresources)) {
            return buildAuthenticatedConfigAttribute();
        }
        List<Role> roles = roleService.list(new QueryWrapper<Role>().in("ROLEID", roleresources.stream().map(roleresource -> roleresource.getRoleid()).collect(Collectors.toList())));
        if (CollectionUtils.isEmpty(roles)) {
            return buildAuthenticatedConfigAttribute();
        }
        return SecurityConfig.createList(roles.stream().map(role -> "ROLE_" + role.getRolename()).toArray(String[]::new));
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    private Collection<ConfigAttribute> buildAuthenticatedConfigAttribute() {
        return SecurityConfig.createList("IS_AUTHENTICATED_FULLY");
    }
}
