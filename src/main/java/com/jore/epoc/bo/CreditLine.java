package com.jore.epoc.bo;

import java.util.Objects;

import org.hibernate.annotations.CompositeType;
import org.hibernate.annotations.Type;

import com.jore.Assert;
import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CreditLine extends BusinessObject {
    private static final int MONTH_PER_YEAR = 12;
    @ManyToOne(optional = false)
    private Company company;
    @Type(com.jore.datatypes.hibernate.PercentUserType.class)
    private Percent interestRate;
    @AttributeOverride(name = "amount", column = @Column(name = "credit_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "credit_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money creditAmount;

    public void adjustAmount(CreditEventDirection direction, Money adjustAmount) {
        Assert.isTrue("Direction must be increase or decrease", direction.equals(CreditEventDirection.INCREASE) || direction.equals(CreditEventDirection.DECREASE));
        creditAmount = direction.equals(CreditEventDirection.INCREASE) ? Money.add(creditAmount, adjustAmount) : Money.max(Money.of("CHF", 0), Money.subtract(creditAmount, adjustAmount));
    }

    public Money getMonthlyInterest() {
        Objects.requireNonNull(creditAmount);
        Objects.requireNonNull(interestRate);
        return creditAmount.multiply(interestRate).divide(MONTH_PER_YEAR);
    }
}
