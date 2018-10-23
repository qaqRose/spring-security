package com.qxq.springsecurity.security.brower;

import lombok.Data;

/**
 * @author: QXQ
 */
@Data
public class SimpleResponse {
    private String content;
    public SimpleResponse(String content){
        this.content = content;
    }
}
