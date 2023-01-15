package com.maoyou.security.security.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * 适配AccessDecisionManager
 */
@Component
public class AccessDecisionManagerAuthorizationManagerAdapter implements AuthorizationManager {
    /**
     * 内置访问决策管理器AccessDecisionManager
     *      AffirmativeBased：任何一个投票者同意
     *      UnanimousBased：所有投票者同意或弃权
     *      ConsensusBased：同意的票数多于反对的票数(忽略弃权)
     * 内置访问决策投票器
     *      WebExpressionVoter
     *      RoleVoter：拥有角色即可
     *      AuthenticatedVoter：通过认证即可
     */
    private AccessDecisionManager accessDecisionManager = new AffirmativeBased(Arrays.asList(
//            new WebExpressionVoter(),
            new RoleVoter(),
            new AuthenticatedVoter()
    ));
    @Autowired
    private SecurityMetadataSource securityMetadataSource;

    @Override
    public AuthorizationDecision check(Supplier authentication, Object object) {
        try {
            Collection<ConfigAttribute> attributes = this.securityMetadataSource.getAttributes(object);
            this.accessDecisionManager.decide((Authentication) authentication.get(), object, attributes);
            return new AuthorizationDecision(true);
        } catch (AccessDeniedException ex) {
            return new AuthorizationDecision(false);
        }
    }

    @Override
    public void verify(Supplier authentication, Object object) {
        Collection<ConfigAttribute> attributes = this.securityMetadataSource.getAttributes(object);
        this.accessDecisionManager.decide((Authentication) authentication.get(), object, attributes);
    }
}
