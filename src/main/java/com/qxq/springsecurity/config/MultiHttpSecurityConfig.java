//package com.qxq.springsecurity.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
///**
// * @author: QXQ
// */
//@Slf4j
//@EnableWebSecurity
//public class MultiHttpSecurityConfig {
//
//    @Configuration
//    @Order(1)
//    public static class ApiWebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            log.info("config  : api");
//            http
//                    .antMatcher("/api/**")
//                    .authorizeRequests()
//                    .anyRequest().hasRole("ADMIN")
//                    .and()
//                    .httpBasic()
//                    .and()
//                    .formLogin()
//                    .loginPage("/nologin")
//                    .permitAll();
//        }
//    }
//    @Configuration
//    @Order(2)
//    public static class FormLoginWebSecurityConfig extends WebSecurityConfigurerAdapter{
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            log.info("config  : form");
//            http
//                    .authorizeRequests()
//                    .anyRequest().authenticated()
//                    .and()
//                    .formLogin()
//                    .loginPage("/login")
//                    .permitAll();
//        }
//    }
//}
