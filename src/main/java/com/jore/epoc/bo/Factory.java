package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.Objects;

import org.hibernate.annotations.CompositeType;

import com.jore.Assert;
import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.accounting.ProductionCostEvent;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Factory extends BusinessObject {
    @ManyToOne(optional = false)
    private Company company;
    private int productionLines;
    private YearMonth productionStartMonth;
    private int monthlyCapacityPerProductionLine;
    @AttributeOverride(name = "amount", column = @Column(name = "production_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "production_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money unitProductionCost;
    @AttributeOverride(name = "amount", column = @Column(name = "labour_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "labour_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money unitLabourCost;

    public int produce(YearMonth productionMonth) {
        Assert.isTrue("Capacity per production line must be greater zero.", monthlyCapacityPerProductionLine > 0);
        Objects.nonNull(productionStartMonth);
        Objects.nonNull(unitProductionCost);
        Objects.nonNull(unitLabourCost);
        int result = !productionStartMonth.isBefore(productionMonth) ? productionLines * monthlyCapacityPerProductionLine : 0;
        ProductionCostEvent bookingEvent = new ProductionCostEvent();
        bookingEvent.setBookingText("Production of " + result + " in month " + productionMonth);
        bookingEvent.setBookingDate(productionMonth.atDay(1));
        bookingEvent.setAmount(unitProductionCost.multiply(result).add(unitLabourCost.multiply(result)));
        company.book(bookingEvent);
        return result;
    }
}
