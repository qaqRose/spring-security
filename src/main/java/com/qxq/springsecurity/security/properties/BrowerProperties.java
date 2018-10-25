package com.qxq.springsecurity.security.properties;

import lombok.Data;

/**
 * 浏览器配置
 * @author: QXQ
 */
public class BrowerProperties {

    /**
     * userDetailsService
     */
    private LoginResponseType loginResponseType = LoginResponseType.JSON;

    /**
     * 记住我时间限制
     */
    private int rememberMeSeconds = 3600;

    public LoginResponseType getLoginResponseType() {
        return loginResponseType;
    }

    public void setLoginResponseType(LoginResponseType loginResponseType) {
        this.loginResponseType = loginResponseType;
    }

    public int getRememberMeSeconds() {
        return rememberMeSeconds;
    }

    public void setRememberMeSeconds(int rememberMeSeconds) {
        this.rememberMeSeconds = rememberMeSeconds;
    }
}
