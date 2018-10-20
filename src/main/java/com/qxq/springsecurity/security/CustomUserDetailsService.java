package com.qxq.springsecurity.security;

import com.qxq.springsecurity.entity.User;
import com.qxq.springsecurity.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

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
        return userFromDatabase.orElseThrow(() -> new UsernameNotFoundException(
                "User " + username + " was not found in the " + "database"));

    }
}
