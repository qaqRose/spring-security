package com.qxq.springsecurity.security.brower;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qxq.springsecurity.security.properties.LoginResponseType;
import com.qxq.springsecurity.security.properties.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录成功处理
 * @author: QXQ
 */
@Slf4j
@Component("customAuthenticationSuccessHandler")
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("登录成功");
        log.info("认证响应方式" +  securityProperties.getBrower().getLoginResponseType().toString());

        if(securityProperties.getBrower().getLoginResponseType().equals(LoginResponseType.JSON)) {
            response.setContentType("application/json;charset=UTF-8");   //返回json格式
            response.getWriter().write(objectMapper.writeValueAsString(authentication)); //将authentication写入到 response
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }
}
