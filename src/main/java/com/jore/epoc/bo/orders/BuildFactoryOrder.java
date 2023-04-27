package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Factory;
import com.jore.epoc.bo.Message;
import com.jore.epoc.bo.MessageLevel;
import com.jore.epoc.bo.accounting.Accounting;
import com.jore.epoc.bo.accounting.BookingRecord;
import com.jore.epoc.bo.accounting.DebitCreditAmount;

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
            Factory factory = new Factory();
            factory.setProductionLines(productionLines);
            factory.setProductionStartMonth(getExecutionMonth().plusMonths(timeToBuild));
            factory.setMonthlyCapacityPerProductionLine(monthlyCapacityPerProductionLine);
            factory.setUnitLabourCost(unitLabourCost);
            factory.setUnitProductionCost(unitProductionCost);
            getCompany().addFactory(factory);
            BookingRecord bookingRecord = new BookingRecord(getExecutionMonth().atDay(FIRST_OF_MONTH), "Built factory", new DebitCreditAmount(Accounting.IMMOBILIEN, Accounting.BANK, constructionCosts.add(constructionCostsPerLine.multiply(productionLines))));
            getCompany().book(bookingRecord);
            setExecuted(true);
        } else {
            this.setExecutionMonth(getExecutionMonth().plusMonths(1));
            Message message = new Message();
            message.setRelevantMonth(getExecutionMonth());
            message.setLevel(MessageLevel.WARNING);
            message.setMessage("Could not create factory due to insufficent funds. Trying next month again.");
            getCompany().addMessage(message);
            log.info(message);
        }
    }

    @Override
    public int getSortOrder() {
        return 4;
    }
}
