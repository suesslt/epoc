package com.jore.epoc.bo;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class UserInCompanyRole extends BusinessObject {
    @ManyToOne(optional = false)
    private Company company;
    @ManyToOne(optional = false)
    private Login user;
    private boolean isInvitationRequired = false;

    public Company getCompany() {
        return company;
    }

    public Login getUser() {
        return user;
    }

    public boolean isInvitationRequired() {
        return isInvitationRequired;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setIsInvitationRequired(boolean isInvitationRequired) {
        this.isInvitationRequired = isInvitationRequired;
    }

    public void setUser(Login user) {
        this.user = user;
    }
}
