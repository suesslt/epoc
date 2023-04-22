package com.jore.epoc.bo.events;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.DistributionInMarket;
import com.jore.epoc.bo.MarketSimulation;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
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
    private int intentedProductSale;
    @AttributeOverride(name = "amount", column = @Column(name = "offered_price_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "offered_price_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money offeredPrice;

    @Override
    public void apply(Company company) {
        DistributionInMarket distributionInMarket = new DistributionInMarket();
        distributionInMarket.setCompany(company);
        distributionInMarket.setOfferedPrice(offeredPrice);
        distributionInMarket.setIntentedProductSale(intentedProductSale);
        marketSimulation.addDistributionInMarket(distributionInMarket);
        company.addDistributionInMarket(distributionInMarket);
    }
}
