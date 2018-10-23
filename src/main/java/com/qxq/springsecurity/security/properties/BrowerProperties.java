package com.qxq.springsecurity.security.properties;

import lombok.Data;

/**
 * @author: QXQ
 */
@Data
public class BrowerProperties {

    // 默认登录响应方式为 JSON
    private LoginResponseType loginResponseType = LoginResponseType.JSON;



}
