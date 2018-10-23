package com.qxq.springsecurity.security.brower;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qxq.springsecurity.security.properties.LoginResponseType;
import com.qxq.springsecurity.security.properties.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 浏览器登录失败处理
 * @author: QXQ
 */
@Slf4j
@Component("customAuthenticationFailureHandler")
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("登录失败");
        // 设置了 json
        if(securityProperties.getBrower().getLoginResponseType().equals(LoginResponseType.JSON)) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());  // 500 状态码
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponse(exception.getMessage())));
        } else  {
            super.onAuthenticationFailure(request, response, exception);
        }

    }
}
