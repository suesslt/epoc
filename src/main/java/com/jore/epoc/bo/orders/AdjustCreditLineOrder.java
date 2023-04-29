package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;
import org.hibernate.annotations.Type;

import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.epoc.bo.CreditEventDirection;
import com.jore.epoc.bo.MessageLevel;
import com.jore.epoc.bo.accounting.FinancialAccounting;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class AdjustCreditLineOrder extends AbstractSimulationOrder {
    private CreditEventDirection direction;
    @AttributeOverride(name = "amount", column = @Column(name = "adjust_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "adjust_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money amount;
    @Type(com.jore.datatypes.hibernate.PercentUserType.class)
    private Percent interestRate;

    @Override
    public void execute() {
        if (direction.equals(CreditEventDirection.INCREASE)) {
            book(getExecutionMonth().atDay(FIRST_OF_MONTH), "Increase credit line by " + amount, FinancialAccounting.BANK, FinancialAccounting.LONG_TERM_DEBT, amount);
            addMessage(String.format("Increased credit line for %s in %s.", amount, getExecutionMonth()), MessageLevel.INFORMATION);
            setExecuted(true);
        } else if (direction.equals(CreditEventDirection.DECREASE)) {
            if (getCompany().getAccounting().checkFunds(amount)) {
                book(getExecutionMonth().atDay(FIRST_OF_MONTH), "Decrease credit line by " + amount, FinancialAccounting.LONG_TERM_DEBT, FinancialAccounting.BANK, amount);
                addMessage(String.format("Decreased credit line for %s in %s.", amount, getExecutionMonth()), MessageLevel.INFORMATION);
                setExecuted(true);
            } else {
                addMessage(String.format("Insufficient funds to decrease credit line. Required %s, available %s.", amount, getCompany().getAccounting().getBankBalance()), MessageLevel.WARNING);
            }
        }
    }

    @Override
    public int getSortOrder() {
        return 1;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public void setDirection(CreditEventDirection direction) {
        this.direction = direction;
    }

    public void setInterestRate(Percent interestRate) {
        this.interestRate = interestRate;
    }
}
