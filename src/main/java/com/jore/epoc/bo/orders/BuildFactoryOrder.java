package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Factory;
import com.jore.epoc.bo.MessageLevel;
import com.jore.epoc.bo.accounting.FinancialAccounting;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class BuildFactoryOrder extends AbstractSimulationOrder {
    private Integer productionLines;
    private Integer timeToBuild;
    private Integer monthlyCapacityPerProductionLine;
    @AttributeOverride(name = "amount", column = @Column(name = "production_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "production_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money unitProductionCost;
    @AttributeOverride(name = "amount", column = @Column(name = "labor_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "labor_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money productionLineLaborCost;
    @AttributeOverride(name = "amount", column = @Column(name = "fixed_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "fixed_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money constructionCosts;
    @AttributeOverride(name = "amount", column = @Column(name = "variable_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "variable_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money constructionCostsPerLine;

    @Override
    public void execute() {
        Money factoryCosts = constructionCosts.add(constructionCostsPerLine.multiply(productionLines));
        if (getCompany().getAccounting().checkFunds(factoryCosts)) {
            addFactory();
            book(getExecutionMonth().atDay(FIRST_OF_MONTH), "Built factory", FinancialAccounting.REAL_ESTATE, FinancialAccounting.BANK, constructionCosts.add(constructionCostsPerLine.multiply(productionLines)));
            addMessage(String.format("Factory created for total cost of %s in %s.", factoryCosts, getExecutionMonth()), MessageLevel.INFORMATION);
            setExecuted(true);
        } else {
            addMessage(String.format("Could not create factory due to insufficent funds in %s. Required were %s, available %s.", getExecutionMonth(), factoryCosts, getCompany().getAccounting().getBankBalance()), MessageLevel.WARNING);
        }
    }

    @Override
    public int getSortOrder() {
        return 4;
    }

    public void setConstructionCosts(Money constructionCosts) {
        this.constructionCosts = constructionCosts;
    }

    public void setConstructionCostsPerLine(Money constructionCostsPerLine) {
        this.constructionCostsPerLine = constructionCostsPerLine;
    }

    public void setMonthlyCapacityPerProductionLine(Integer monthlyCapacityPerProductionLine) {
        this.monthlyCapacityPerProductionLine = monthlyCapacityPerProductionLine;
    }

    public void setProductionLineLaborCost(Money productionLineLaborCost) {
        this.productionLineLaborCost = productionLineLaborCost;
    }

    public void setProductionLines(int productionLines) {
        this.productionLines = productionLines;
    }

    public void setTimeToBuild(Integer timeToBuild) {
        this.timeToBuild = timeToBuild;
    }

    public void setUnitProductionCost(Money unitProductionCosts) {
        unitProductionCost = unitProductionCosts;
    }

    private void addFactory() {
        Factory factory = new Factory();
        factory.setProductionLines(productionLines);
        factory.setProductionStartMonth(getExecutionMonth().plusMonths(timeToBuild));
        factory.setMonthlyCapacityPerProductionLine(monthlyCapacityPerProductionLine);
        factory.setProductionLineLaborCost(productionLineLaborCost);
        factory.setUnitProductionCost(unitProductionCost);
        getCompany().addFactory(factory);
    }
}
