package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.Message;
import com.jore.epoc.bo.MessageLevel;
import com.jore.epoc.bo.Storage;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
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
        Money storageCosts = getFixedCosts().add(getVariableCosts().multiply(capacity));
        if (company.checkFunds(storageCosts)) {
            Storage storage = new Storage();
            storage.setCapacity(capacity);
            storage.setStorageStartMonth(getExecutionMonth().plusMonths(timeToBuild));
            storage.setStorageCostPerUnitAndMonth(storageCostPerUnitAndMonth);
            company.addStorage(storage);
        } else {
            this.setExecutionMonth(getExecutionMonth().plusMonths(1));
            Message message = new Message();
            message.setRelevantMonth(getExecutionMonth());
            message.setLevel(MessageLevel.WARNING);
            message.setMessage("Could not create storage due to insufficent funds. Trying next month again.");
            company.addMessage(message);
            log.info(message);
        }
    }

    @Override
    public int getSortOrder() {
        return 2;
    }
}
