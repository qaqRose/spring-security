package com.qxq.springsecurity.security.validate.code;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码异常
 * 继承 `AuthenticationException` 权限异常
 * @author: QXQ
 */
public class ValidateCodeException extends AuthenticationException {

    public ValidateCodeException(String msg) {
        super(msg);
    }
}
