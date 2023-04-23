package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.Factory;
import com.jore.epoc.bo.accounting.BuildInfrastructureBookingEvent;

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

    @Override
    public void apply(Company company) {
        Factory factory = new Factory();
        factory.setProductionLines(productionLines);
        factory.setProductionStartMonth(getExecutionMonth().plusMonths(timeToBuild));
        factory.setMonthlyCapacityPerProductionLine(monthlyCapacityPerProductionLine);
        factory.setUnitLabourCost(unitLabourCost);
        factory.setUnitProductionCost(unitProductionCost);
        company.addFactory(factory);
        BuildInfrastructureBookingEvent bookingEvent = new BuildInfrastructureBookingEvent();
        bookingEvent.setBookingText("Factory construction");
        bookingEvent.setBookingDate(getExecutionMonth().atDay(FIRST_OF_MONTH));
        bookingEvent.setAmount(getFixedCosts().add(getVariableCosts().multiply(productionLines)));
        company.book(bookingEvent);
    }
}