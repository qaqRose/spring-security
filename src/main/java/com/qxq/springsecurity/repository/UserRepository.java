package com.qxq.springsecurity.repository;

import com.qxq.springsecurity.entity.User;

import java.util.Optional;

/**
 * @author: QXQ
 */
public interface UserRepository extends CustomRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
