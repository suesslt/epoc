package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;
import org.hibernate.annotations.Type;

import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.CreditEventDirection;
import com.jore.epoc.bo.CreditLine;

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
    public void apply(Company company) {
        CreditLine creditLine = company.getCreditLine();
        if (creditLine == null) {
            creditLine = new CreditLine();
            creditLine.setCompany(company);
            creditLine.setInterestRate(interestRate);
            company.setCreditLine(creditLine);
        }
        creditLine.adjustAmount(direction, adjustAmount);
    }
}
