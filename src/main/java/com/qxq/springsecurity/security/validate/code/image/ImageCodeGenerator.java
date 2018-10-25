package com.qxq.springsecurity.security.validate.code.image;

import com.qxq.springsecurity.security.properties.SecurityProperties;
import com.qxq.springsecurity.security.validate.code.ValidateCodeGenerator;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author: QXQ
 */
@Component("imageCodeGenerator")
public class ImageCodeGenerator implements ValidateCodeGenerator {

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 生成一张带验证码的图片
     * @param request
     * @return
     */
    @Override
    public ImageCode generate(HttpServletRequest request) {
        int width = ServletRequestUtils.getIntParameter(request, "width", securityProperties.getCode().getImage()
                .getWidth());
        int height = ServletRequestUtils.getIntParameter(request, "height", securityProperties.getCode().getImage()
                .getHeight());

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = image.getGraphics();

        Random random = new Random();

        g.setColor(getRandColor(200, 250));  // 设置颜色
        g.fillRect(0, 0, width, height);        // 填充画布
        g.setFont(new Font("Times New Roman", Font.ITALIC, 20));  //设置字体
        g.setColor(getRandColor(160, 200));     // 设置颜色
        for (int i = 0; i < 155; i++) {   // 划线
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        //验证码
        String sRand = "";
        for (int i = 0; i < securityProperties.getCode().getImage()
                .getLength(); i++) {
            String rand = String.valueOf(random.nextInt(10));  // 4个10内的随机数作为验证码
            sRand += rand;
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 13 * i + 6, 16);
        }

        g.dispose();

        return new ImageCode(image, sRand, securityProperties.getCode().getImage()
                .getRepireIn());
    }

    //生成一个随机颜色
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }
}
