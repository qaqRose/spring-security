package com.qxq.springsecurity.security.validate.code;

import com.qxq.springsecurity.security.validate.code.image.ImageCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;
import java.util.Map;

/**
 * @author: QXQ
 */
@Component("validateCodeProcessor")
public abstract class AbstractValidateCodeProcessor<T extends ValidateCode> implements ValidateCodeProcessor{

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();
    /**
     * 验证码生成器
     */
    @Autowired
    private Map<String, ValidateCodeGenerator> validateCodeGenerators;

    /**
     * 创建检验码
     */
    @Override
    public void create(ServletWebRequest request) throws IOException {
        //生成
        T validateCode = generator(request);
        //保存
        save(request, validateCode);
        //发送
        send(request, validateCode);

    }


    /**
     * 生成验证码的抽象方法
     * @return
     */
    public T generator(ServletWebRequest request) {
        String type = getProcessorType(request);
        ValidateCodeGenerator validateCodeGenerator = validateCodeGenerators.get(type + "CodeGenerator");
        return (T) validateCodeGenerator.generate(request.getRequest());
    }

    /**
     * 发送验证码的抽象方法
     */
    public abstract void send(ServletWebRequest request, ValidateCode validateCode) throws IOException;

    /**
     * 保存验证码的抽象方法
     */
    public void save(ServletWebRequest request, ValidateCode validateCode) {
        sessionStrategy.setAttribute(new ServletWebRequest(request.getRequest()), SESSION_KEY_PREFIX + getProcessorType(request).toUpperCase(), validateCode);
    }

    private String getProcessorType(ServletWebRequest request) {
        String url = request.getRequest().getRequestURI();
        return StringUtils.substringAfter(url, "/code/");
    }

}
