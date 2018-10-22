package com.qxq.springsecurity.repository;

import com.qxq.springsecurity.entity.Authority;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author: QXQ
 */
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
