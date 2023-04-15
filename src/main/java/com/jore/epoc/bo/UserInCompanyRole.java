package com.jore.epoc.bo;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserInCompanyRole extends BusinessObject {
    @ManyToOne(optional = false)
    private Company company;
    @ManyToOne(optional = false)
    private Login user;
    private boolean invitationRequired = false;
}
