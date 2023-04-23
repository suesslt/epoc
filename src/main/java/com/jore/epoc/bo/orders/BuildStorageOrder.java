package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.Storage;
import com.jore.epoc.bo.accounting.BuildInfrastructureBookingEvent;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BuildStorageOrder extends AbstractSimulationOrder {
    private Integer capacity;
    private Integer timeToBuild;
    @AttributeOverride(name = "amount", column = @Column(name = "storage_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "storage_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money storageCostPerUnitAndMonth;

    @Override
    public void apply(Company company) {
        Storage storage = new Storage();
        storage.setCapacity(capacity);
        storage.setStorageStartMonth(getExecutionMonth().plusMonths(timeToBuild));
        storage.setStorageCostPerUnitAndMonth(storageCostPerUnitAndMonth);
        company.addStorage(storage);
        BuildInfrastructureBookingEvent bookingEvent = new BuildInfrastructureBookingEvent();
        bookingEvent.setBookingText("Storage construction");
        bookingEvent.setBookingDate(getExecutionMonth().atDay(FIRST_OF_MONTH));
        bookingEvent.setAmount(getFixedCosts().add(getVariableCosts().multiply(capacity)));
        company.book(bookingEvent);
    }
}
