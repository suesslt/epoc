package com.jore.epoc.bo.events;

import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.Market;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DistributeInMarketEvent extends AbstractSimulationEvent {
    @ManyToOne(optional = true)
    private Market market;

    @Override
    public void apply(Company company) {
        // TODO Auto-generated method stub
    }
}
