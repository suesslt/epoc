package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.accounting.FinancialAccounting;
import com.jore.epoc.bo.message.MessageLevel;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class MarketingCampaignOrder extends AbstractSimulationOrder {
    @AttributeOverride(name = "amount", column = @Column(name = "marketing_campaign_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "marketing_amount_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money marketingCampaignAmount;

    @Override
    public void execute() {
        if (getCompany().getAccounting().checkFunds(marketingCampaignAmount)) {
            runMarketingCampaign();
            book(getExecutionMonth().atDay(1), "Marketing campaign", FinancialAccounting.SERVICES, FinancialAccounting.BANK, marketingCampaignAmount);
            addMessage(MessageLevel.INFORMATION, "MarketingCampaign", getExecutionMonth(), marketingCampaignAmount);
            setExecuted(true);
        } else {
            addMessage(MessageLevel.WARNING, "NoMarketingCampaignDueToFunding", getExecutionMonth(), marketingCampaignAmount, getCompany().getAccounting().getBankBalance());
        }
    }

    @Override
    public int getSortOrder() {
        return 8;
    }

    public void setAmount(Money marketingCampaignAmount) {
        this.marketingCampaignAmount = marketingCampaignAmount;
    }

    private void runMarketingCampaign() {
        Money pricePerMarketingCampaign = getCompany().getSimulation().getSettings().getPricePerMarketingCampaign();
        double increase = marketingCampaignAmount.divide(pricePerMarketingCampaign).doubleValue();
        increase /= 100;
        getCompany().setMarketingFactor(getCompany().getMarketingFactor() + increase);
    }
}
