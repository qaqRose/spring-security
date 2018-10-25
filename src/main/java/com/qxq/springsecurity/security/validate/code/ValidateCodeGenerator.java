package com.qxq.springsecurity.security.validate.code;

import com.qxq.springsecurity.security.validate.code.image.ImageCode;

import javax.servlet.http.HttpServletRequest;

/**
 * 验证码生成器接口
 * @author: QXQ
 */
public interface ValidateCodeGenerator {
    /**
     * 验证码生成器
     * @param request
     * @return
     */
    ValidateCode generate(HttpServletRequest request);
}
