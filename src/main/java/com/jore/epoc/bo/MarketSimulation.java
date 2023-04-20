package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Entity
@Getter
@Setter
public class MarketSimulation extends BusinessObject {
    @ManyToOne(optional = false)
    private Market market;
    @ManyToOne(optional = false)
    private Simulation simulation;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "marketSimulation", orphanRemoval = true)
    private List<DistributionInMarket> distributionInMarkets = new ArrayList<>();

    public void addDistributionInMarket(DistributionInMarket distributionInMarket) {
        distributionInMarket.setMarketSimulation(this);
        distributionInMarkets.add(distributionInMarket);
    }

    public void simulateMarket(YearMonth simulationMonth) {
        log.info("*** Simulate market " + market.getName() + " distribution in markets: " + distributionInMarkets.size());
    }
}
