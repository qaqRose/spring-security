package com.qxq.springsecurity.security.validate.code;

import com.qxq.springsecurity.security.properties.SecurityProperties;
import com.qxq.springsecurity.security.validate.code.image.ImageCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

/**
 * @author: QXQ
 */
@RestController
public class ValidateCodeController {

    @Autowired
    private Map<String,ValidateCodeProcessor> validateCodeProcessors;

    /**
     * 返回验证码
     * @param request
     * @throws IOException
     */
    @GetMapping("/code/{type}")
    public void imageCode(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) throws IOException {
        validateCodeProcessors.get(type + "CodeProcessor").create(new ServletWebRequest(request, response));
    }


}
