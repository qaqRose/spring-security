package com.qxq.springsecurity.config;

import com.qxq.springsecurity.security.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.thymeleaf.expression.Maps;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: QXQ
 */
@Slf4j
@Configuration
@EnableWebSecurity

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("config : single");
        http
                .authorizeRequests()
                .antMatchers("/", "/home").permitAll()  // 不需要任何权限
//                .antMatchers("/user/**").authenticated()   //需要验证身份

//                .antMatchers("/user/**").hasRole("USER")
//                .antMatchers("/admin/**").hasRole("ADMIN")
//                .antMatchers("/h2-console/**").access("hasRole('ADMIN') and hasRole('DBA')")
                .anyRequest().authenticated()  //All other paths must be authenticated
                .and()
                .formLogin()
                .loginPage("/login")   //设置登录页面
                .permitAll();

        http
                .csrf()   //开启csrf防护
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) //将令牌存储在Cookie,可以使用js访问
                .and()
//                .csrf().disable()
                .logout()
                .logoutUrl("/logout") //自定义登出url
                .logoutSuccessUrl("/home")   //登出成功后跳转url
                .invalidateHttpSession(true);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    UserDetailsService customUserService() {
        return new CustomUserDetailsService();
    }



    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public FilterRegistrationBean csrfFilter() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new CsrfFilter(new HttpSessionCsrfTokenRepository()));
//        registration.addUrlPatterns("/*");
//        return registration;
//    }


    /**
     * 生成两个用户存储在内存中
     * @return
     */
//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
////        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        PasswordEncoder encoder = passwordEncoder();
//        UserDetails user =
//                User.builder()    //创建一个USER角色  的用户
//                        .username("user")
//                        .password("password")
//                        .passwordEncoder(encoder::encode)
//                        .roles("USER")
//                        .build();
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password("password")
//                .passwordEncoder(encoder::encode)
//                .roles("ADMIN")
//                .build();
//        log.info("user :" +user.getPassword());
//        log.info("admin :" +admin.getPassword());
//        return new InMemoryUserDetailsManager(user,admin);    // 存储在内存中
//    }
}
