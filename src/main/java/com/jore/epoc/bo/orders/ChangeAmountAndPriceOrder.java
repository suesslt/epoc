package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.DistributionInMarket;
import com.jore.epoc.bo.Market;
import com.jore.epoc.bo.MessageLevel;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ChangeAmountAndPriceOrder extends AbstractSimulationOrder {
    private Integer intentedSales;
    @AttributeOverride(name = "amount", column = @Column(name = "price_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "price_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money offeredPrice;
    @ManyToOne(optional = true)
    private Market market;

    @Override
    public void execute() {
        DistributionInMarket distributionInMarket = getCompany().getDistributionInMarkets().stream().filter(distribution -> distribution.getMarketSimulation().getMarket().equals(market)).findFirst().get();
        distributionInMarket.setIntentedProductSale(intentedSales);
        distributionInMarket.setOfferedPrice(offeredPrice);
        addMessage("Changed Intented Product Sales and Offered Price", MessageLevel.INFORMATION);
    }

    @Override
    public int getSortOrder() {
        return 6;
    }
}
