package com.qxq.springsecurity.security;

import com.qxq.springsecurity.entity.Authority;
import com.qxq.springsecurity.repository.AuthorityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 获取安全元数据服务
 * 即获取权限信息
 * @author: QXQ
 */
@Slf4j
@Service
public class CustomInvocationSecurityMetadataSourceService implements FilterInvocationSecurityMetadataSource  {
    @Autowired
    private AuthorityRepository authorityRepository;

    private HashMap<String, Collection<ConfigAttribute>> map =null;

    public void loadAllAuthorities() {
        log.info("加载所有权限");
        map = new HashMap<>();
        Collection<ConfigAttribute> array;
        ConfigAttribute cfg;
        List<Authority> authorities = authorityRepository.findAll();

        for(Authority authority : authorities) {
            log.info("权限 {}", authority.toString() );
            array = new ArrayList<>();
            cfg = new SecurityConfig(authority.getName());
            //此处只添加了用户的名字，其实还可以添加更多权限的信息，
            // 例如请求方法到ConfigAttribute的集合中去。
            // 此处添加的信息将会作为CustomAccessDecisionManager类的decide的第三个参数。
            array.add(cfg);
            //用权限的getUrl() 作为map的key，用ConfigAttribute的集合作为 value，
            map.put(authority.getUrl(), array);
        }


    }

    /**
     * 判断用户请求的url 是否在权限表中
     * 如果在权限中,则让CustomAccessDecisionManager.decide()方法判断用户是否拥有权限
     * @param object
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        log.info("获取配置属性");
        if(map == null) loadAllAuthorities();
        //object 中包含用户请求的request 信息
        HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
        log.info("当前访问url : {}", request.getServletPath());
        AntPathRequestMatcher matcher;
        String resUrl;
        for(Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
            // map 中保存数据库所有权限
            // key  url
            // value  所需的权限
            resUrl = iter.next();
            matcher = new AntPathRequestMatcher(resUrl);
            if(matcher.matches(request)) {
                // 当前访问的url 匹配到 权限所需的url
                log.info("匹配到所需权限");
                return map.get(resUrl);
            }
        }
        log.error("数据库没有此 url 对应的权限信息");
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
