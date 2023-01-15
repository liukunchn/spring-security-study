package com.maoyou.security.security.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Supplier;

import static org.springframework.security.access.AccessDecisionVoter.ACCESS_DENIED;
import static org.springframework.security.access.AccessDecisionVoter.ACCESS_GRANTED;


/**
 * 适配AccessDecisionVoter
 */
@Component
public class AccessDecisionVoterAuthorizationManagerAdapter implements AuthorizationManager {
    private AccessDecisionVoter accessDecisionVoter = new RoleVoter();;
    @Autowired
    private SecurityMetadataSource securityMetadataSource;

    @Override
    public AuthorizationDecision check(Supplier authentication, Object object) {
        Collection<ConfigAttribute> attributes = this.securityMetadataSource.getAttributes(object);
        int decision = this.accessDecisionVoter.vote((Authentication) authentication.get(), object, attributes);
        switch (decision) {
            case ACCESS_GRANTED:
                return new AuthorizationDecision(true);
            case ACCESS_DENIED:
                return new AuthorizationDecision(false);
        }
        return null;
    }

}
