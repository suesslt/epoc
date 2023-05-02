package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.DistributionInMarket;
import com.jore.epoc.bo.MarketSimulation;
import com.jore.epoc.bo.accounting.FinancialAccounting;
import com.jore.epoc.bo.message.MessageLevel;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class EnterMarketOrder extends AbstractSimulationOrder {
    @ManyToOne(optional = true) // TODO Why optional?
    private MarketSimulation marketSimulation;
    private int intentedProductSale;
    @AttributeOverride(name = "amount", column = @Column(name = "offered_price_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "offered_price_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money offeredPrice;
    @AttributeOverride(name = "amount", column = @Column(name = "market_entry_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "market_entry_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money marketEntryCost;

    @Override
    public void execute() {
        if (getCompany().getAccounting().checkFunds(marketEntryCost)) {
            addDistributionInMarket();
            book(getExecutionMonth().atDay(1), "Entry into market", FinancialAccounting.SERVICES, FinancialAccounting.BANK, marketEntryCost);
            addMessage(String.format("Successfully set up entry into market %s.", marketSimulation.getMarket().getName()), MessageLevel.INFORMATION);
            setExecuted(true);
        } else {
            addMessage(String.format("Could not enter market %s due to insufficient funding", marketSimulation.getMarket().getName()), MessageLevel.WARNING);
        }
    }

    @Override
    public int getSortOrder() {
        return 5;
    }

    public void setEnterMarktCost(Money marketEntryCost) {
        this.marketEntryCost = marketEntryCost;
    }

    public void setIntentedProductSale(Integer intentedProductSale) {
        this.intentedProductSale = intentedProductSale;
    }

    public void setMarketSimulation(MarketSimulation marketSimulation) {
        this.marketSimulation = marketSimulation;
    }

    public void setOfferedPrice(Money offeredPrice) {
        this.offeredPrice = offeredPrice;
    }

    private void addDistributionInMarket() {
        DistributionInMarket distributionInMarket = new DistributionInMarket();
        distributionInMarket.setOfferedPrice(offeredPrice);
        distributionInMarket.setIntentedProductSale(intentedProductSale);
        marketSimulation.addDistributionInMarket(distributionInMarket);
        getCompany().addDistributionInMarket(distributionInMarket);
    }
}
