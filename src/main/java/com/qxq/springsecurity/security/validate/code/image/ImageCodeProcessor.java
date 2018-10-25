package com.qxq.springsecurity.security.validate.code.image;


import com.qxq.springsecurity.security.validate.code.AbstractValidateCodeProcessor;
import com.qxq.springsecurity.security.validate.code.ValidateCode;
import com.qxq.springsecurity.security.validate.code.ValidateCodeException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * @author: QXQ
 */
@Component("imageCodeProcessor")
public class ImageCodeProcessor extends AbstractValidateCodeProcessor<ImageCode> {

    @Override
    public void send(ServletWebRequest request, ValidateCode validateCode) throws IOException {
        ImageIO.write(((ImageCode)validateCode).getImage(), "JPEG", request.getResponse().getOutputStream());
    }

//    @Override
//    public void validated(ServletWebRequest request) throws ServletRequestBindingException, ValidateCodeException {
//
//    }
}
