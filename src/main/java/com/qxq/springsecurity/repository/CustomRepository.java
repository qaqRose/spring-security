package com.qxq.springsecurity.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author: QXQ
 */
@NoRepositoryBean
public interface CustomRepository<T, ID> extends JpaRepository<T, ID> {
}
