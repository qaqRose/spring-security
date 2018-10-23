package com.qxq.springsecurity.security.properties;

/**
 * 登录成功后的响应方式
 * @author: QXQ
 */
public enum LoginResponseType {
    /**
     * 跳转方式
     */
    REDIRECT,
    /**
     * JSON 格式响应
     */
    JSON
}
