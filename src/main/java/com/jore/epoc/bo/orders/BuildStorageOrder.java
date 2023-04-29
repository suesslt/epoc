package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.MessageLevel;
import com.jore.epoc.bo.Storage;
import com.jore.epoc.bo.accounting.FinancialAccounting;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class BuildStorageOrder extends AbstractSimulationOrder {
    private Integer capacity;
    private Integer timeToBuild;
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
        if (getCompany().getAccounting().checkFunds(storageCosts)) {
            addStorage();
            book(getExecutionMonth().atDay(FIRST_OF_MONTH), "Built storage", FinancialAccounting.REAL_ESTATE, FinancialAccounting.BANK, constructionCosts.add(constructionCostsPerUnit.multiply(capacity)));
            addMessage(String.format("Build Storage for capacity of %s units in %s.", capacity, getExecutionMonth()), MessageLevel.INFORMATION);
            setExecuted(true);
        } else {
            addMessage(String.format("Could not create storage due to insufficent funds in %s. Required were %s, available %s.", getExecutionMonth(), storageCosts, getCompany().getAccounting().getBankBalance()), MessageLevel.WARNING);
        }
    }

    @Override
    public int getSortOrder() {
        return 2;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setConstructionCosts(Money constructionCosts) {
        this.constructionCosts = constructionCosts;
    }

    public void setConstructionCostsPerUnit(Money constructionCostsPerUnit) {
        this.constructionCostsPerUnit = constructionCostsPerUnit;
    }

    public void setTimeToBuild(Integer timeToBuild) {
        this.timeToBuild = timeToBuild;
    }

    private void addStorage() {
        Storage storage = new Storage();
        storage.setCapacity(capacity);
        storage.setStorageStartMonth(getExecutionMonth().plusMonths(timeToBuild));
        getCompany().addStorage(storage);
    }
}
