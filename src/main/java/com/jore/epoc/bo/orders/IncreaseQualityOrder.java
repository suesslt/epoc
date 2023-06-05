package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.accounting.FinancialAccounting;
import com.jore.epoc.bo.message.MessageLevel;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class IncreaseQualityOrder extends AbstractSimulationOrder {
    @AttributeOverride(name = "amount", column = @Column(name = "increase_quality_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "increase_quality_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money increaseQualityAmount;

    @Override
    public void execute() {
        if (getCompany().getAccounting().checkFunds(increaseQualityAmount, getExecutionMonth().atEndOfMonth())) {
            increaseQuality();
            book(getExecutionMonth().atDay(1), "Increase quality", FinancialAccounting.SERVICES, FinancialAccounting.BANK, increaseQualityAmount);
            addMessage(MessageLevel.INFORMATION, "IncreaseQuality", getExecutionMonth(), increaseQualityAmount);
            setExecuted(true);
        } else {
            addMessage(MessageLevel.WARNING, "NoIncreaseInQualityDueToFunding", getExecutionMonth(), increaseQualityAmount, getCompany().getAccounting().getBankBalance(getExecutionMonth().atEndOfMonth()));
        }
    }

    @Override
    public Money getAmount() {
        return increaseQualityAmount;
    }

    @Override
    public int getSortOrder() {
        return 7;
    }

    @Override
    public String getType() {
        return "Increase quality";
    }

    public void setAmount(Money increaseQualityAmount) {
        this.increaseQualityAmount = increaseQualityAmount;
    }

    private void increaseQuality() {
        Money pricePerPercentPoint = getCompany().getSimulation().getSettings().getPricePerPercentPointQuality();
        double increase = increaseQualityAmount.divide(pricePerPercentPoint).doubleValue();
        increase /= 100;
        getCompany().setQualityFactor(getCompany().getQualityFactor() + increase);
    }
}
