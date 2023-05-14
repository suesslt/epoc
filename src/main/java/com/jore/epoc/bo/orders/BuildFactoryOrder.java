package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Factory;
import com.jore.epoc.bo.accounting.FinancialAccounting;
import com.jore.epoc.bo.message.MessageLevel;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class BuildFactoryOrder extends AbstractSimulationOrder {
    private Integer productionLines;
    private Integer timeToBuild;
    private Integer dailyCapacityPerProductionLine;
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
        if (getCompany().getAccounting().checkFunds(factoryCosts, getExecutionMonth().atEndOfMonth())) {
            addFactory();
            book(getExecutionMonth().atDay(FIRST_OF_MONTH), "Built factory", FinancialAccounting.REAL_ESTATE, FinancialAccounting.BANK, constructionCosts.add(constructionCostsPerLine.multiply(productionLines)));
            addMessage(MessageLevel.INFORMATION, "FactoryCreated", factoryCosts, getExecutionMonth());
            setExecuted(true);
        } else {
            addMessage(MessageLevel.WARNING, "NoFactoryDueToFunds", getExecutionMonth(), factoryCosts, getCompany().getAccounting().getBankBalance(getExecutionMonth().atEndOfMonth()));
        }
    }

    @Override
    public int getSortOrder() {
        return 4;
    }

    public void setConstructionCost(Money constructionCosts) {
        this.constructionCosts = constructionCosts;
    }

    public void setConstructionCostPerLine(Money constructionCostsPerLine) {
        this.constructionCostsPerLine = constructionCostsPerLine;
    }

    public void setDailyCapacityPerProductionLine(Integer dailyCapacityPerProductionLine) {
        this.dailyCapacityPerProductionLine = dailyCapacityPerProductionLine;
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

    private void addFactory() {
        Factory factory = new Factory();
        factory.setProductionLines(productionLines);
        factory.setProductionStartMonth(getExecutionMonth().plusMonths(timeToBuild));
        factory.setDailyCapacityPerProductionLine(dailyCapacityPerProductionLine);
        factory.setProductionLineLaborCost(productionLineLaborCost);
        getCompany().addFactory(factory);
    }
}
