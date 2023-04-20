package com.jore.epoc.bo;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DistributionInMarket extends BusinessObject {
    @ManyToOne(optional = false)
    private Company company;
    @ManyToOne
    private MarketSimulation marketSimulation;
}
