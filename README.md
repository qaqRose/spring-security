### 基于角色权限管理

## 01 流程

当用户登录时,系统会调用`UserDetailsService`获取用户信息,保存用户信息到`SecurityContext`全局缓存,以备后面调用

当用户请求url资源时, 会被`AbstractSecurityInterceptor`的实现类拦截,并调用`FilterInvocationSecurityMetadataSource`的实现类 来获取被拦截url需要什么权限,在`AccessDecisionManager`的实现类中通过全局缓存`SecurityContext`获取当前用户的权限,在对权限进行匹配


## 02 思路

### `UserDetailsService` 获取登录用户信息

首先自定义`CustomUserDetailsService`实现`UserDetailsService`
```
public interface UserDetailsService {
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```

`UserDetailsService`里面是有一个方法`loadUserByUsername`,我们实现这个方法获取用户信息,并封装在一个`org.springframework.security.core.userdetails.User`类中并返回

这个对象将被存储在`SecurityContext`中,后面操作都里面获取用户用信息,包含权限

### `FilterInvocationSecurityMetadataSource`

一个接口

用来获取被拦截url需要的权限

继承了  `SecurityMetadataSource`

```
public interface FilterInvocationSecurityMetadataSource extends SecurityMetadataSource {
}

```
而 `SecurityMetadataSource` 又继承了 `AopInfrastructureBean`
```
public interface SecurityMetadataSource extends AopInfrastructureBean {

	Collection<ConfigAttribute> getAttributes(Object object)
			throws IllegalArgumentException;

	Collection<ConfigAttribute> getAllConfigAttributes();

	boolean supports(Class<?> clazz);
}

```
`getAttributes()` 返回所需权限列表

### `AccessDecisionManager`
`AccessDecisionManager`是一个接口,主要用进行权限匹配

```
public interface AccessDecisionManager {
    void decide(Authentication authentication, Object object,
			Collection<ConfigAttribute> configAttributes) throws AccessDeniedException,
			InsufficientAuthenticationException;
			
    boolean supports(ConfigAttribute attribute);
    
    boolean supports(Class<?> clazz);
}

```
其中 decide()方法用户判断是否拥有权限
`authentication` 是 `USER`对象中权限的集合
`object` 则是客户端的请求对象request
`configAttributes` 使用`FilterInvocationSecurityMetadataSource`中`getAttributes()`返回的结果,表示需要的权限

当匹配上权限时,表示用户用户此权限

当没有可匹配项时,则放行


###  `AbstractSecurityInterceptor` 抽象类,需要继承并实现Filter,过滤url,设置`AccessDecisionManager`



## 03 设计表

- 用户表
- 角色表
- 权限
- 用户角色中间表
- 角色权限中间表

分别有这5个表

使用`Spring-Boot-Jpa`生成数据表,则对应的实体类为
file : User.java
```

package com.qxq.springsecurity.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

/**
 * @author: QXQ
 */
@Table(name = "user")
@Entity
@Data
public class User implements UserDetails  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<Role> roles;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```
角色类
file : Role.java

```
package com.qxq.springsecurity.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author: QXQ
 */
@Table(name = "role")
@Entity
@Data
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_authority", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {@JoinColumn(name = "authority_id")})
    private List<Authority> authorities;

}

```

权限类

file : Authority.java

```
package com.qxq.springsecurity.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author: QXQ
 */
@Table(name = "authority")
@Entity
@Data
public class Authority implements Serializable,GrantedAuthority  {

    public interface AuthoritySimpleView{} ;

    public interface AuthorityDetailView extends AuthoritySimpleView{} ;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(AuthoritySimpleView.class)
    private Integer id;

    @JsonView(AuthoritySimpleView.class)
    private String name;

    @JsonView(AuthorityDetailView.class)
    private String description;

    @JsonView(AuthoritySimpleView.class)
    private String url;

    @JsonView(AuthorityDetailView.class)
    private Integer pid;

    @Override
    public String getAuthority() {
        return this.url;
    }
}

```

## 04 实现Repository

```
public interface AuthorityRepository extends CustomRepository<Authority, Integer> {

    /**
     * 通过用户名查找权限
     * @param id
     * @return
     */
    @Query(value = "select e.* from user a \n" +
            "LEFT JOIN user_role b ON a.id = b.user_id\n" +
            "LEFT JOIN role c ON b.role_id = c.id\n" +
            "left JOIN role_authority d ON c.id = d.role_id\n" +
            "LEFT JOIN authority e on d.authority_id = e.id\n" +
            "where a.id = ?1",nativeQuery = true)
    public List<Authority> findAllByUserId(Integer id);


}
```

其他仅继承接口,没有方法
## 05 WebSecurity配置

```
package com.qxq.springsecurity.config;

import com.qxq.springsecurity.security.CustomFilterSecurityInterceptor;
import com.qxq.springsecurity.security.CustomUserDetailsService;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

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
        http
                .authorizeRequests()
                .antMatchers("/", "/home").permitAll()  // 不需要任何权限
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

        http.addFilterBefore(filterSecurityInterceptor, FilterSecurityInterceptor.class);
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


```


## 06 自定义UserDetailsService
```
package com.qxq.springsecurity.security;

import com.qxq.springsecurity.entity.Authority;
import com.qxq.springsecurity.entity.User;
import com.qxq.springsecurity.repository.AuthorityRepository;
import com.qxq.springsecurity.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author: QXQ
 */
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final  AuthorityRepository authorityRepository;

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("userDetailsService :  查找 {}",username);
        Optional<User> userFromDatabase = userRepository.findByUsername(username);
        User user = userFromDatabase.orElseThrow(() -> new UsernameNotFoundException(
                "用户 " + username + " 查无此人"));
        log.info("user : {} ", user.toString());
        // 使用认证权限
        List<Authority> authorities = authorityRepository.findAllByUserId(user.getId());
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        // 使用 authority的 name 作为权限的标志
        for(Authority authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
        }

        // 构造一个User 供系统调用
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), grantedAuthorities);

    }
}

```

## 07 实现FilterSecurityInterceptor

```
package com.qxq.springsecurity.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Service;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author: QXQ
 */
@Slf4j
@Service
public class CustomFilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {
    @Autowired
    private FilterInvocationSecurityMetadataSource securityMetadataSource;

    @Autowired
    public void setMyAccessDecisionManager(CustomAccessDecisionManager accessDecisionManager) {
        super.setAccessDecisionManager(accessDecisionManager);
    }


    @Override
    public Class<?> getSecureObjectClass() {
        return  FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        FilterInvocation filterInvocation = new FilterInvocation(servletRequest, servletResponse, filterChain);
        log.warn("CustomFilterSecurityInterceptor  拦截器");
        //filterInvocation里面有一个被拦截的url
        //里面调用CustomInvocationSecurityMetadataSourceService的getAttributes(Object object)
        //获取所需要的权限
        //再调用CustomAccessDecisionManager的decide方法来校验用户的权限是否足够
        invoke(filterInvocation);


    }

    public void invoke(FilterInvocation filterInvocation) throws IOException, ServletException {
        InterceptorStatusToken token = super.beforeInvocation(filterInvocation);
        try {
            //执行下一个拦截器
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
        } finally {
            super.afterInvocation(token, null);
        }
    }

    @Override
    public void destroy() {

    }
}

```

## 08  实现 FilterInvocationSecurityMetadataSource

```
package com.qxq.springsecurity.security;

import com.qxq.springsecurity.entity.Authority;
import com.qxq.springsecurity.repository.AuthorityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author: QXQ
 */
@Slf4j
@Service
public class CustomInvocationSecurityMetadataSourceService implements FilterInvocationSecurityMetadataSource  {
    @Autowired
    private AuthorityRepository authorityRepository;

    private HashMap<String, Collection<ConfigAttribute>> map =null;

    public void loadAllAuthorities() {
        log.info("加载所有权限");
        map = new HashMap<>();
        Collection<ConfigAttribute> array;
        ConfigAttribute cfg;
        List<Authority> authorities = authorityRepository.findAll();

        for(Authority authority : authorities) {
            log.info("权限 {}", authority.toString() );
            array = new ArrayList<>();
            cfg = new SecurityConfig(authority.getName());
            //此处只添加了用户的名字，其实还可以添加更多权限的信息，
            // 例如请求方法到ConfigAttribute的集合中去。
            // 此处添加的信息将会作为CustomAccessDecisionManager类的decide的第三个参数。
            array.add(cfg);
            //用权限的getUrl() 作为map的key，用ConfigAttribute的集合作为 value，
            map.put(authority.getUrl(), array);
        }


    }

    /**
     * 判断用户请求的url 是否在权限表中
     * 如果在权限中,则让CustomAccessDecisionManager.decide()方法判断用户是否拥有权限
     * @param object
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        log.info("获取配置属性");
        if(map == null) loadAllAuthorities();
        //object 中包含用户请求的request 信息
        HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
        log.info("当前访问url : {}", request.getServletPath());
        AntPathRequestMatcher matcher;
        String resUrl;
        for(Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
            // map 中保存数据库所有权限
            // key  url
            // value  所需的权限
            resUrl = iter.next();
            matcher = new AntPathRequestMatcher(resUrl);
            if(matcher.matches(request)) {
                // 当前访问的url 匹配到 权限所需的url
                log.info("匹配到所需权限");
                return map.get(resUrl);
            }
        }
        log.error("数据库没有此 url 对应的权限信息");
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
```

## 09 实现`AccessDecisionManager`
```
package com.qxq.springsecurity.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author: QXQ
 */
@Slf4j
@Service
public class CustomAccessDecisionManager implements AccessDecisionManager {
    /**
     *  判定是否拥有权限的决策方法，
     *  authentication 是 CustomUserDetailsService 中 用户的 authorities
     *   object 包含客户端发起的请求的requset信息
     *   configAttributes 为 MyInvocationSecurityMetadataSource的getAttributes(Object object)这个方法返回的结果，
     *   此方法是为了判定用户请求的url 是否在权限表中，如果在权限表中,则返回给 decide 方法，用来判定用户是否有此权限。如果不在权限表中则放行
     * @param authentication
     * @param object
     * @param configAttributes
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        log.info("验证权限");
        if(null== configAttributes || configAttributes.size() <=0) {
            return;
        }
        ConfigAttribute c;
        String needRole;
        for(Iterator<ConfigAttribute> iter = configAttributes.iterator(); iter.hasNext(); ) {
            c = iter.next();
            needRole = c.getAttribute();
            // 比较用户所有权限 与当前页面访问需要的权限
            for(GrantedAuthority ga : authentication.getAuthorities()) {//authentication 为在注释1 中循环添加到 GrantedAuthority 对象中的权限信息集合
                if(needRole.trim().equals(ga.getAuthority())) {
                    log.info("拥有访问页面的权限 {}", needRole);
                    return;
                }
            }
        }
        log.error("没有权限");
        throw new AccessDeniedException("no authority");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
```
