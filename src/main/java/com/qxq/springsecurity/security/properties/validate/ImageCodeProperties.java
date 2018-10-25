package com.qxq.springsecurity.security.properties.validate;

import lombok.Data;

/**
 * 图形验证码
 * @author: QXQ
 */
@Data
public class ImageCodeProperties {
    /**
     * 宽度
     */
    private int width = 67;
    /**
     * 高度
     */
    private int height = 23;
    /**
     * 长度
     */
    private int length = 4;
    /**
     * 失效时间
     */
    private int repireIn = 60;

    private String urls;
}
