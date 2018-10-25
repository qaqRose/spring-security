package com.qxq.springsecurity.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author: QXQ
 */
@Slf4j
@Service
public class  CustomAccessDecisionManager implements AccessDecisionManager {
    /**
     *  判定是否拥有权限的决策方法，
     *  authentication 是 CustomUserDetailsService 中 用户的 authorities
     *   object 包含客户端发起的请求的requset信息
     *   configAttributes 为 MyInvocationSecurityMetadataSource的getAttributes(Object object)这个方法返回的结果，
     *   此方法是为了判定用户请求的url 是否在权限表中，如果在权限表中,则返回给 decide 方法，用来判定用户是否有此权限。如果不在权限表中则放行
     * @param authentication
     * @param object
     * @param configAttributes
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        log.info("验证权限");
        if(null== configAttributes || configAttributes.size() <=0) {
            return;
        }
        ConfigAttribute c;
        String needRole;
        for(Iterator<ConfigAttribute> iter = configAttributes.iterator(); iter.hasNext(); ) {
            c = iter.next();
            needRole = c.getAttribute();
            // 比较用户所有权限 与当前页面访问需要的权限
            for(GrantedAuthority ga : authentication.getAuthorities()) {//authentication 为在注释1 中循环添加到 GrantedAuthority 对象中的权限信息集合
                if(needRole.trim().equals(ga.getAuthority())) {
                    log.info("拥有访问页面的权限 {}", needRole);
                    return;
                }
            }
        }
        log.error("没有权限");
        throw new AccessDeniedException("no authority");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
