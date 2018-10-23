package com.qxq.springsecurity.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: QXQ
 */
@Data
@ConfigurationProperties(prefix = "qxq.security")
public class SecurityProperties {
    private BrowerProperties brower = new BrowerProperties();
}
