package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.MessageLevel;
import com.jore.epoc.bo.Storage;
import com.jore.epoc.bo.accounting.FinancialAccounting;

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
    @AttributeOverride(name = "amount", column = @Column(name = "fixed_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "fixed_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money constructionCosts;
    @AttributeOverride(name = "amount", column = @Column(name = "variable_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "variable_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money constructionCostsPerUnit;

    @Override
    public void execute() {
        Money storageCosts = constructionCosts.add(constructionCostsPerUnit.multiply(capacity));
        if (getCompany().checkFunds(storageCosts)) {
            addStorage();
            book(getExecutionMonth().atDay(FIRST_OF_MONTH), "Built storage", FinancialAccounting.IMMOBILIEN, FinancialAccounting.BANK, constructionCosts.add(constructionCostsPerUnit.multiply(capacity)));
            addMessage("Storage added", MessageLevel.INFORMATION);
            setExecuted(true);
        } else {
            addMessage("Could not create storage due to insufficent funds. Trying next month again.", MessageLevel.WARNING);
        }
    }

    @Override
    public int getSortOrder() {
        return 2;
    }

    private void addStorage() {
        Storage storage = new Storage();
        storage.setCapacity(capacity);
        storage.setStorageStartMonth(getExecutionMonth().plusMonths(timeToBuild));
        storage.setStorageCostPerUnitAndMonth(storageCostPerUnitAndMonth);
        getCompany().addStorage(storage);
    }
}
