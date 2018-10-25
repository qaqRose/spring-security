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
 * 自定义用户详情
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
