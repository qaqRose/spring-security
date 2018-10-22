
# 简单权限认证


## 01 定义User对象 和 Role对象

由于 Spring Security 

是使用`AuthenticationManager`对当前主体进行验证管理

而`AuthenticationManagerBuilder`使用`UserDetailsService`管理用户实例

而`UserDetailsService` 只有一个方法`loadUserByUsername()`返回 `UserDetails`对象

`UserDetails` 是一个接口, 继承了`Serializable`,所以对象是可序列化的

我们定义的User 需要实现 `UserDetails`

```
public class User implements UserDetails  {
    private Integer id;
    private String username;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<Role> roles;
    
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

当然还要需要有角色表    

```
@Table(name = "role")
@Entity
@Data
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

}

```

使用 `@Data` 减轻压力

## 02 定义 Repository

使用jpa

```
public interface UserRepository extends CustomRepository<User, Integer> {
    Optional<User> findByUsername(String username);

}
```

`CustomRepository` 如下,继承了`JpaRepository`
后面的`Repository`全部继承`CustomReposioty`方便扩展

```
@NoRepositoryBean
public interface CustomRepository<T, ID> extends JpaRepository<T, ID> {
}
```


## 03 配置路径访问权限

这里使用的角色验证

```
.antMatchers("/user/**").hasRole("USER")
.antMatchers("/admin/**").hasRole("ADMIN")
```

即访问 `/user/**` 需要有 `ROLE_USER` 权限

即访问 `/admin/**` 需要有 `ROLE_ADMIN` 权限


## 03 自定义 UserDetailsService

spring security 验证权限会从全局缓存`SecurityContextHolder`中获取上下文关系,最后通过`UserDetailsService`获取到当前用户

我们需要自定义`UserDetailsService` 并从数据库中获取用户信息

这就是我们的目的

```
@Slf4j
@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("userDetailsService :  查找 {}",username);
        Optional<User> userFromDatabase = userRepository.findByUsername(username);
        User user = userFromDatabase.get();
        log.info("user : {} ", user.toString());
        // 使用认证权限
        // 验证的用户的角色role
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for(Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        // 构造一个User 供系统调用
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), authorities);
//        return userFromDatabase.orElseThrow(() -> new UsernameNotFoundException(
//                "User " + username + " was not found in the " + "database"));

    }
```

由于spring security 是使用`GrantedAuthority`来表示权限

这里我们使用`SimpleGrantedAuthority`,

`SimpleGrantedAuthority`是`GrantedAuthority`的一个简单实现,

以字符串的表示形式来验证权限

这里我们使用角色名称来表示权限

即 User对象的roles列表的元素 表示 该对象拥有此权限

**注意** 由于,在匹配觉得url时,使用`hasRole("USER")`,实际上数据需要存储为`ROLE_USER`才表示拥有此权限

将自定义UserDetailsService注册到SpringBoot `Bean`缓冲中
```
@Bean
UserDetailsService customUserService() {   // 注册bean
    return new CustomUserDetailsService();
}
```  

将UserDetailsService做为设置到权限管理

```
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth
            .userDetailsService(new CustomUserDetailsService())  //设置自定义UserDetailsService
            .passwordEncoder(passwordEncoder());    //自定义加密方式
}

```


简单权限管理就完成了
