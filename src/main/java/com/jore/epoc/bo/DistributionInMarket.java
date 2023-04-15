package com.jore.epoc.bo;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class DistributionInMarket extends BusinessObject {
    @ManyToOne(optional = false)
    private Company company;
}
