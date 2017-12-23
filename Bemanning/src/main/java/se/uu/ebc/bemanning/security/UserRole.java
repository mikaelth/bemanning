package se.uu.ebc.bemanning.security;

import javax.persistence.Column;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import se.uu.ebc.bemanning.entity.Person;

 @Entity
 @Table(name = "USER_ROLE")
 public class UserRole {

    @ManyToOne
    @JoinColumn(name = "user_fk")
    private Person user;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    
    @Column(name = "ROLE", length = 255)
    @NotNull
    private String role;
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }


    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public Person getUser() {
        return this.user;
    }
    
    public void setUser(Person user) {
        this.user = user;
    }
}
