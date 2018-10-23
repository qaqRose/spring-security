package com.qxq.springsecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

/**
 * @author: QXQ
 */
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    /**
     * 设置页面跳转对应的视图
     * 对于不需要设置逻辑控制的页面比较方便
     * @param registry
     */
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/login").setViewName("login");
    }



}
