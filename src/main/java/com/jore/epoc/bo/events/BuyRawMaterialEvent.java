package com.jore.epoc.bo.events;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BuyRawMaterialEvent extends SimulationEvent {
    private Integer amount;
    @AttributeOverride(name = "amount", column = @Column(name = "unit_price_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "unit_price_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money unitPrice;

    @Override
    public void apply(Company company) {
        // TODO Auto-generated method stub
    }
}
