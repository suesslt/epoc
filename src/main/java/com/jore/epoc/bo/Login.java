package com.jore.epoc.bo;

import java.util.ArrayList;
import java.util.List;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Login extends BusinessObject {
    @Column(unique = true)
    private String login;
    private String password;
    private String name;
    private String email;
    private boolean isAdmin;
    @ManyToOne(cascade = CascadeType.ALL, optional = true)
    private Simulation simulation;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<UserInCompanyRole> companies = new ArrayList<>();

    public void addCompanyRole(UserInCompanyRole userInCompanyRole) {
        companies.add(userInCompanyRole);
    }

    @Override
    public String toString() {
        return "Login [login=" + login + ", password=" + password + ", name=" + name + ", email=" + email + ", isAdmin=" + isAdmin + "]";
    }
}
