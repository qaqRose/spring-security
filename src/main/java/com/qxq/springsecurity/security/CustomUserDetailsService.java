package com.qxq.springsecurity.security;

import com.qxq.springsecurity.entity.Role;
import com.qxq.springsecurity.entity.User;
import com.qxq.springsecurity.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author: QXQ
 */
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
}
