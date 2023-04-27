package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
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
public class EnterMarketOrder extends AbstractSimulationOrder {
    @ManyToOne(optional = true)
    private MarketSimulation marketSimulation;
    private int intentedProductSale;
    @AttributeOverride(name = "amount", column = @Column(name = "offered_price_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "offered_price_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money offeredPrice;
    @AttributeOverride(name = "amount", column = @Column(name = "fixed_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "fixed_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money fixedCosts;
    @AttributeOverride(name = "amount", column = @Column(name = "variable_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "variable_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money variableCosts;

    @Override
    public void apply() {
        DistributionInMarket distributionInMarket = new DistributionInMarket();
        distributionInMarket.setOfferedPrice(offeredPrice);
        distributionInMarket.setIntentedProductSale(intentedProductSale);
        marketSimulation.addDistributionInMarket(distributionInMarket);
        getCompany().addDistributionInMarket(distributionInMarket);
        setExecuted(true);
    }

    @Override
    public int getSortOrder() {
        return 5;
    }
}
