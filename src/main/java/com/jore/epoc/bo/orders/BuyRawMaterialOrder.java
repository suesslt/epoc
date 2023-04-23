package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.Assert;
import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.Storage;
import com.jore.epoc.bo.accounting.BuyRawMaterialBookingEvent;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BuyRawMaterialOrder extends AbstractSimulationOrder {
    private Integer amount;
    @AttributeOverride(name = "amount", column = @Column(name = "unit_price_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "unit_price_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money unitPrice;

    // TODO it might be necessaire to sort simulation events in case a new storage is immediately built
    @Override
    public void apply(Company company) {
        Assert.notNull("Amount must not be null", amount);
        Assert.notNull("Unit price must not be null", unitPrice);
        int storageCapacity = company.getStorages().stream().mapToInt(storage -> storage.getAvailableCapacity(getExecutionMonth())).sum();
        if (storageCapacity > 0) {
            BuyRawMaterialBookingEvent buyRawMaterialBookingEvent = new BuyRawMaterialBookingEvent();
            buyRawMaterialBookingEvent.setBookingText("Buy " + storageCapacity + " units of raw material");
            buyRawMaterialBookingEvent.setBookingDate(getExecutionMonth().atDay(FIRST_OF_MONTH));
            buyRawMaterialBookingEvent.setAmount(unitPrice.multiply(storageCapacity));
            company.book(buyRawMaterialBookingEvent);
            Storage.distributeRawMaterialAccrossStorages(company.getStorages(), storageCapacity, getExecutionMonth());
        }
    }
}
