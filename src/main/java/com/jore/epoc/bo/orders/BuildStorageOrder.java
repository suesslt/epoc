package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Storage;
import com.jore.epoc.bo.accounting.FinancialAccounting;
import com.jore.epoc.bo.message.MessageLevel;

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
    @AttributeOverride(name = "amount", column = @Column(name = "inventory_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "inventory_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money inventoryManagementCost;

    @Override
    public void execute() {
        if (getCompany().getAccounting().checkFunds(getAmount(), getExecutionMonth().atEndOfMonth())) {
            addStorage();
            book(getExecutionMonth().atDay(FIRST_OF_MONTH), "Built storage", FinancialAccounting.REAL_ESTATE, FinancialAccounting.BANK, constructionCosts.add(constructionCostsPerUnit.multiply(capacity)));
            addMessage(MessageLevel.INFORMATION, "StorageBuilt", capacity, getExecutionMonth());
            setExecuted(true);
        } else {
            addMessage(MessageLevel.WARNING, "NoStorageDueToFunds", getExecutionMonth(), getAmount(), getCompany().getAccounting().getBankBalance(getExecutionMonth().atEndOfMonth()));
        }
    }

    @Override
    public Money getAmount() {
        return constructionCosts.add(constructionCostsPerUnit.multiply(capacity));
    }

    public Money getInventoryManagementCost() {
        return inventoryManagementCost;
    }

    @Override
    public int getSortOrder() {
        return 2;
    }

    @Override
    public String getType() {
        return "Build Storage";
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setConstructionCost(Money constructionCosts) {
        this.constructionCosts = constructionCosts;
    }

    public void setConstructionCostPerUnit(Money constructionCostsPerUnit) {
        this.constructionCostsPerUnit = constructionCostsPerUnit;
    }

    public void setInventoryManagementCost(Money inventoryManagementCost) {
        this.inventoryManagementCost = inventoryManagementCost;
    }

    public void setTimeToBuild(Integer timeToBuild) {
        this.timeToBuild = timeToBuild;
    }

    private void addStorage() {
        Storage storage = new Storage();
        storage.setCapacity(capacity);
        storage.setStorageStartMonth(getExecutionMonth().plusMonths(timeToBuild));
        storage.setInventoryManagementCost(inventoryManagementCost);
        getCompany().addStorage(storage);
    }
}
