package com.jore.epoc.bo.events;

import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.DistributionInMarket;
import com.jore.epoc.bo.MarketSimulation;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DistributeInMarketEvent extends AbstractSimulationEvent {
    @ManyToOne(optional = true)
    private MarketSimulation marketSimulation;

    @Override
    public void apply(Company company) {
        DistributionInMarket distributionInMarket = new DistributionInMarket();
        distributionInMarket.setCompany(company);
        marketSimulation.addDistributionInMarket(distributionInMarket);
        company.addDistributionInMarket(distributionInMarket);
    }
}
