package com.jore.epoc.bo.user;

import java.util.ArrayList;
import java.util.List;

import com.jore.epoc.bo.Simulation;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 */
@Entity(name = "Login")
public class User extends BusinessObject {
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

    public List<UserInCompanyRole> getCompanies() {
        return companies;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setCompanies(List<UserInCompanyRole> companies) {
        this.companies = companies;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public String toString() {
        return "Login [login=" + login + ", password=" + password + ", name=" + name + ", email=" + email + ", isAdmin=" + isAdmin + "]";
    }
}
