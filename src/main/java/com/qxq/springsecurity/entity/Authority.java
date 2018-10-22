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
