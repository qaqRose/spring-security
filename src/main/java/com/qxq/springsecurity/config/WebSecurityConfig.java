package com.qxq.springsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author: QXQ
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/home").permitAll()  // 不需要任何权限
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/h2-console/**").access("hasRole('ADMIN') and hasRole('DBA')")
                .anyRequest().authenticated()  //All other paths must be authenticated
                .and()
                .formLogin()
//                .and()
//                .httpBasic();
                .loginPage("/login")   //设置登录页面
                .permitAll()
                .and()
                .logout()               // 登出
                .permitAll();
    }


    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserDetails user =
                User.builder()    //创建一个USER角色的用户
                        .username("user")
                        .password("password")
                        .passwordEncoder(encoder::encode)
                        .roles("USER")
                        .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password("admin")
                .passwordEncoder(encoder::encode)
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user,admin);    // 存储在内存中
    }
}
