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
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AdjustCreditLineOrder extends AbstractSimulationOrder {
    private CreditEventDirection direction;
    @AttributeOverride(name = "amount", column = @Column(name = "adjust_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "adjust_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money adjustAmount;
    @Type(com.jore.datatypes.hibernate.PercentUserType.class)
    private Percent interestRate;

    @Override
    public void execute() {
        if (direction.equals(CreditEventDirection.INCREASE)) {
            book(getExecutionMonth().atDay(FIRST_OF_MONTH), "Increase credit line by " + adjustAmount, FinancialAccounting.BANK, FinancialAccounting.LONG_TERM_DEBT, adjustAmount);
        } else {
            book(getExecutionMonth().atDay(FIRST_OF_MONTH), "Decrease credit line by " + adjustAmount, FinancialAccounting.LONG_TERM_DEBT, FinancialAccounting.BANK, adjustAmount);
        }
        addMessage(String.format("%s credit line for %s.", direction, adjustAmount), MessageLevel.INFORMATION);
        setExecuted(true);
    }

    @Override
    public int getSortOrder() {
        return 1;
    }
}
