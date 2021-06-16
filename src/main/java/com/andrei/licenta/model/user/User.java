package com.andrei.licenta.model.user;

import com.andrei.licenta.model.Anunt;
import com.fasterxml.jackson.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false,updatable = false)
    private Long id;
    @Column(nullable = false,length = 50,unique = true)
    private String email;
    @Column(nullable = false,length = 30)
    private String firstName;
    @Column(nullable = false,length = 30)
    private String lastName;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false,length = 10)
    private String role;
    @Column(nullable = false)
    private String[] authorities;
    @Column(nullable = false)
    private Boolean isNonLocked = true;
    @Column(nullable = false)
    private Boolean isActive = false;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Anunt> anunturi;



    public User(){}

    public User(Long id, String email, String firstName, String lastName,
                String password, String role, String[] authorities,
                Boolean isNonLocked, Boolean isActive, List<Anunt> anunturi) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
        this.isNonLocked = isNonLocked;
        this.isActive = isActive;
        this.anunturi = anunturi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String[] getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = authorities;
    }

    public Boolean getIsNonLocked() {
        return isNonLocked;
    }

    public void setIsNonLocked(Boolean nonLocked) {
        isNonLocked = nonLocked;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public List<Anunt> getAnunturi() {
        return anunturi;
    }

    public void setAnunturi(List<Anunt> anunturi) {
        this.anunturi = anunturi;
    }

}
