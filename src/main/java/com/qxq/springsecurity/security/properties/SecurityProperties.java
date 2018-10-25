package com.qxq.springsecurity.security.properties;

import com.qxq.springsecurity.security.properties.validate.ValidateCodeProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 安全配置
 * @author: QXQ
 */
@Data
@ConfigurationProperties(prefix = "qxq.security")
public class SecurityProperties {

    private BrowerProperties brower = new BrowerProperties();

    private ValidateCodeProperties code = new ValidateCodeProperties();
}
