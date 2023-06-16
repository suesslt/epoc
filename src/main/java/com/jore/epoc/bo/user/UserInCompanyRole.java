package com.jore.epoc.bo.user;

import com.jore.epoc.bo.Company;
import com.jore.jpa.AbstractBusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

// TODO User and company combination must be unique
@Entity
public class UserInCompanyRole extends AbstractBusinessObject {
    @ManyToOne(optional = false)
    private Company company;
    @ManyToOne(optional = false)
    private User user;
    private boolean isInvitationRequired = false;

    public Company getCompany() {
        return company;
    }

    public User getUser() {
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

    public void setUser(User user) {
        this.user = user;
    }
}
