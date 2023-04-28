package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Factory;
import com.jore.epoc.bo.MessageLevel;
import com.jore.epoc.bo.accounting.FinancialAccounting;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BuildFactoryOrder extends AbstractSimulationOrder {
    private Integer productionLines;
    private Integer timeToBuild;
    private Integer monthlyCapacityPerProductionLine;
    @AttributeOverride(name = "amount", column = @Column(name = "production_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "production_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money unitProductionCost;
    @AttributeOverride(name = "amount", column = @Column(name = "labour_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "labour_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money unitLabourCost;
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
        if (getCompany().checkFunds(factoryCosts)) {
            addFactory();
            book(getExecutionMonth().atDay(FIRST_OF_MONTH), "Built factory", FinancialAccounting.IMMOBILIEN, FinancialAccounting.BANK, constructionCosts.add(constructionCostsPerLine.multiply(productionLines)));
            addMessage("Factory created.", MessageLevel.INFORMATION);
            setExecuted(true);
        } else {
            addMessage("Could not create factory due to insufficent funds. Trying next month again.", MessageLevel.WARNING);
        }
    }

    @Override
    public int getSortOrder() {
        return 4;
    }

    private void addFactory() {
        Factory factory = new Factory();
        factory.setProductionLines(productionLines);
        factory.setProductionStartMonth(getExecutionMonth().plusMonths(timeToBuild));
        factory.setMonthlyCapacityPerProductionLine(monthlyCapacityPerProductionLine);
        factory.setUnitLabourCost(unitLabourCost);
        factory.setUnitProductionCost(unitProductionCost);
        getCompany().addFactory(factory);
    }
}
