package com.qxq.springsecurity.security.validate.code;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;

/**
 * 验证码处理器接口
 * @author: QXQ
 */
public interface ValidateCodeProcessor {
    /**
     * session key 的前缀
     */
    String SESSION_KEY_PREFIX = "SESSION_KEY_FOR_CODE_";
    /**
     * 生成验证码
     */
    void create(ServletWebRequest request) throws IOException;

//    /**
//     * 校对验证码
//     */
//    void validated(ServletWebRequest request);

}
