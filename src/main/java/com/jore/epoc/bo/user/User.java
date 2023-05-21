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
    private String password;
    private String email;
    private boolean isAdmin;
    @ManyToOne(cascade = CascadeType.ALL, optional = true)
    private Simulation simulation;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<UserInCompanyRole> companies = new ArrayList<>();
    private String username;
    private String firstName;
    private String lastName;
    private String phone;

    public void addCompanyRole(UserInCompanyRole userInCompanyRole) {
        companies.add(userInCompanyRole);
    }

    public List<UserInCompanyRole> getCompanies() {
        return companies;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Login [username=" + username + ", password=" + password + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", phone=" + phone + ", isAdmin=" + isAdmin + "]";
    }
}
