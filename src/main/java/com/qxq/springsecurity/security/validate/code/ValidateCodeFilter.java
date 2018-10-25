package com.qxq.springsecurity.security.validate.code;

import com.qxq.springsecurity.security.properties.SecurityProperties;
import com.qxq.springsecurity.security.validate.code.image.ImageCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 验证码过滤器
 * 继承`OncePerRequestFilter` 表示只使用一次
 * @author: QXQ
 */
@Slf4j
@Data
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {

    // 认证失败处理器
    private AuthenticationFailureHandler authenticationFailureHandler;

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    private SecurityProperties securityProperties;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    private Set<String> urls = new HashSet<>();

    /**
     *
     * @throws ServletException
     */
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        String[] configUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(securityProperties.getCode().getImage().getUrls(), ",");
        for(String configUrl : configUrls) {

            urls.add(configUrl);
        }
        urls.add("/auth/form");
        log.info("图形验证码拦截url : {} ",ArrayUtils.toString(urls.toArray()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean flag = false;
        log.warn("匹配url : {}" + request.getRequestURI());
        for (String url : urls) {
            if(antPathMatcher.match(url, request.getRequestURI())) {
                flag = true;
                break;
            }
        }
        //
        if(flag){
            try{
                validate(new ServletWebRequest(request));
            } catch (ValidateCodeException e){
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return ;
            }

        }
        // 下一个过滤器
        filterChain.doFilter(request, response);
    }

    private void validate(ServletWebRequest request) throws ServletRequestBindingException {
        ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(request, ValidateCodeProcessor.SESSION_KEY_PREFIX + "IMAGE");
        String codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(), "imageCode");

        if(codeInRequest == null) {
            throw new ValidateCodeException("验证码不存在");
        }

        if ("".equals(codeInRequest)) {
            throw new ValidateCodeException("验证码为空");
        }

        if(codeInSession.isExpired()) {
            throw new ValidateCodeException("验证码过期");
        }

        if(!codeInRequest.equals(codeInSession.getCode())) {
            throw new ValidateCodeException("验证码不匹配");
        }



        sessionStrategy.removeAttribute(request, ValidateCodeProcessor.SESSION_KEY_PREFIX + "IMAGE");
    }
}
