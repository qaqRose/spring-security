package com.qxq.springsecurity.security.validate.code;

import org.springframework.security.core.AuthenticationException;

/**
 * @author: QXQ
 */
public class ValidateCodeException extends AuthenticationException {

    public ValidateCodeException(String msg) {
        super(msg);
    }
}
