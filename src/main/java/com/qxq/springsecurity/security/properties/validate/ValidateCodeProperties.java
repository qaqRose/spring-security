package com.qxq.springsecurity.security.properties.validate;

import lombok.Data;

/**
 * 验证码配置
 * @author: QXQ
 */
@Data
public class ValidateCodeProperties {

    private ImageCodeProperties image = new ImageCodeProperties();

}
