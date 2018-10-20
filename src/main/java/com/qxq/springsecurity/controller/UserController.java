package com.qxq.springsecurity.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * @author: QXQ
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {
    @ResponseBody
    @RequestMapping("/info")
    public String getUsername() {
        // 获取当前主体信息
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        log.info("username :" + username);
        return username;
    }
    @ResponseBody
    @RequestMapping("/name")
    public String getRemoteUser(HttpServletRequest request) {
        String username = request.getRemoteUser();
        log.info("username :" + username);
        return username;
    }
    @ResponseBody
    @RequestMapping("/prin")
    public String getUserPrincipal(HttpServletRequest request) {
//        Authentication auth = (Authentication)request.getUserPrincipal();

//        auth.getAuthorities().stream().forEach(System.out::println);

//        Principal principal = (Principal)auth.getPrincipal();

//        String username = principal.getName();
//        log.info("username :" + username);


        return username;
    }

//    @RequestMapping("/csrf")
//    public String csrfTest() {
//        return "csrfTest";
//    }
}
