package com.jore.epoc.bo.accounting;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
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
public class Account extends BusinessObject {
    @ManyToOne(optional = false)
    private FinancialAccounting accounting;
    private String number;
    @AttributeOverride(name = "amount", column = @Column(name = "balance_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "balance_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money balance;

    public Account() {
    }

    public void credit(Money amount) {
        balance = Money.subtract(balance, amount);
    }

    public void debit(Money amount) {
        balance = Money.add(balance, amount);
    }
}