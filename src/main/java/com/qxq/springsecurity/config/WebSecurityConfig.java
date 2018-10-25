package com.qxq.springsecurity.config;

import com.qxq.springsecurity.security.CustomFilterSecurityInterceptor;
import com.qxq.springsecurity.security.CustomUserDetailsService;
import com.qxq.springsecurity.security.properties.SecurityProperties;
import com.qxq.springsecurity.security.validate.code.ValidateCodeFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.sql.DataSource;

/**
 * @author: QXQ
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomFilterSecurityInterceptor filterSecurityInterceptor;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private DataSource dataSource;

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
//        jdbcTokenRepository.setCreateTableOnStartup(true);  //第一次需要自动创建表
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 以下资源  spring security 不会对其进行拦截
        web
                .ignoring()
                .antMatchers("/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("config : single");
        // 添加验证码过滤器
        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        validateCodeFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        validateCodeFilter.setSecurityProperties(securityProperties);
        validateCodeFilter.afterPropertiesSet();

        http
                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(filterSecurityInterceptor, FilterSecurityInterceptor.class)
                .authorizeRequests()
                    .antMatchers("/", "/home", "/code/image").permitAll()  // 不需要任何权限
                    .anyRequest().authenticated()  //All other paths must be authenticated
                .and()
                .formLogin()
                    .loginPage("/login")   //设置登录页面
                    .loginProcessingUrl("/auth/form")
                    .successHandler(customAuthenticationSuccessHandler)
                    .failureHandler(customAuthenticationFailureHandler)
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

        http
                .rememberMe()
                .tokenValiditySeconds(securityProperties.getBrower().getRememberMeSeconds())
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(userDetailsService);

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)  //设置自定义UserDetailsService
                .passwordEncoder(passwordEncoder());    //自定义加密方式
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
