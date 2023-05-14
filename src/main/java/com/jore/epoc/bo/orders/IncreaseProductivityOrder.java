package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.accounting.FinancialAccounting;
import com.jore.epoc.bo.message.MessageLevel;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class IncreaseProductivityOrder extends AbstractSimulationOrder {
    @AttributeOverride(name = "amount", column = @Column(name = "increase_productivity_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "increase_productivity_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money increaseProductivityAmount;

    @Override
    public void execute() {
        if (getCompany().getAccounting().checkFunds(increaseProductivityAmount, getExecutionMonth().atEndOfMonth())) {
            setProductivityFactor();
            book(getExecutionMonth().atDay(1), "Increase productivity", FinancialAccounting.SERVICES, FinancialAccounting.BANK, increaseProductivityAmount);
            addMessage(MessageLevel.INFORMATION, "IncreaseProductivity", getExecutionMonth(), increaseProductivityAmount);
            setExecuted(true);
        } else {
            addMessage(MessageLevel.WARNING, "NoIncreaseInProductivityDueToFunding", getExecutionMonth(), increaseProductivityAmount, getCompany().getAccounting().getBankBalance(getExecutionMonth().atEndOfMonth()));
        }
    }

    @Override
    public int getSortOrder() {
        return 7;
    }

    public void setAmount(Money increaseProductivityAmount) {
        this.increaseProductivityAmount = increaseProductivityAmount;
    }

    private void setProductivityFactor() {
        Money pricePerPercentPoint = getCompany().getSimulation().getSettings().getPricePerPercentPointProductivity();
        double increase = increaseProductivityAmount.divide(pricePerPercentPoint).doubleValue();
        increase /= 100;
        getCompany().setProductivityFactor(getCompany().getProductivityFactor() + increase);
    }
}
