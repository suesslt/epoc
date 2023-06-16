package com.jore.epoc.bo;

import java.time.LocalDate;
import java.time.YearMonth;

import org.hibernate.annotations.CompositeType;
import org.hibernate.annotations.Type;

import com.jore.Assert;
import com.jore.datatypes.money.Money;
import com.jore.jpa.AbstractBusinessObject;
import com.jore.util.Util;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Factory extends AbstractBusinessObject {
    @ManyToOne(optional = false)
    private Company company;
    private int productionLines;
    @Type(com.jore.datatypes.hibernate.YearMonthUserType.class)
    private YearMonth productionStartMonth;
    private int dailyCapacityPerProductionLine;
    @AttributeOverride(name = "amount", column = @Column(name = "labour_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "labour_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money productionLineLaborCost;

    public Money getProductionCost() {
        return productionLineLaborCost.multiply(productionLines);
    }

    public int produce(int maximumToProduce, YearMonth productionMonth, double productivityFactor) {
        Assert.isTrue("Capacity per production line must be greater zero.", dailyCapacityPerProductionLine > 0);
        Assert.notNull("Production start month must not be null", productionStartMonth);
        int result = 0;
        if (isProductionReady(productionMonth) && Storage.getTotalRawMaterialStored(company.getStorages()) > 0) {
            for (LocalDate date : Util.getDaysInMonth(productionMonth)) {
                if (EpocCalendar.getInstance().isWorkingDay(date)) {
                    int amountRemoved = Storage.removeRawMaterialFromStorages(company.getStorages(), (int) (productionLines * dailyCapacityPerProductionLine * productivityFactor));
                    // Produce
                    result += amountRemoved;
                    Storage.distributeProductAccrossStorages(company.getStorages(), amountRemoved, productionMonth);
                }
            }
        }
        return result;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setDailyCapacityPerProductionLine(int dailyCapacityPerProductionLine) {
        this.dailyCapacityPerProductionLine = dailyCapacityPerProductionLine;
    }

    public void setProductionLineLaborCost(Money productionLineLaborCost) {
        this.productionLineLaborCost = productionLineLaborCost;
    }

    public void setProductionLines(int productionLines) {
        this.productionLines = productionLines;
    }

    public void setProductionStartMonth(YearMonth productionStartMonth) {
        this.productionStartMonth = productionStartMonth;
    }

    private boolean isProductionReady(YearMonth productionMonth) {
        return !productionMonth.isBefore(productionStartMonth);
    }
}
