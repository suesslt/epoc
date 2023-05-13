package com.jore.epoc.bo;

import java.time.LocalDate;
import java.time.YearMonth;

import org.hibernate.annotations.CompositeType;

import com.jore.Assert;
import com.jore.datatypes.money.Money;
import com.jore.jpa.BusinessObject;
import com.jore.util.Util;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Entity
public class Factory extends BusinessObject {
    @ManyToOne(optional = false)
    private Company company;
    private int productionLines;
    private YearMonth productionStartMonth;
    private int monthlyCapacityPerProductionLine;
    @AttributeOverride(name = "amount", column = @Column(name = "labour_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "labour_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money productionLineLaborCost;

    public Money getProductionCost() {
        return productionLineLaborCost.multiply(productionLines);
    }

    public int produce(int maximumToProduce, YearMonth productionMonth, double productivityFactor) {
        Assert.isTrue("Capacity per production line must be greater zero.", monthlyCapacityPerProductionLine > 0);
        Assert.notNull("Production start month must not be null", productionStartMonth);
        Assert.notNull("Production line labour costs must not be null", productionLineLaborCost);
        if (isProductionReady(productionMonth)) {
            for (LocalDate date : Util.getDaysInMonth(productionMonth)) {
                if (EpocCalendar.getInstance().isWorkingDay(date)) {
                    // take raw material from storage
                    // produce
                    // put product into storage
                    log.info(date);
                }
            }
        }
        // return produced amount
        int result = (int) (isProductionReady(productionMonth) ? Math.min(maximumToProduce, productionLines * monthlyCapacityPerProductionLine * productivityFactor) : 0);
        result = Math.min(result, company.getStorages().stream().mapToInt(storage -> storage.getStoredRawMaterials()).sum());
        if (result > 0) {
        }
        return result;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setMonthlyCapacityPerProductionLine(int monthlyCapacityPerProductionLine) {
        this.monthlyCapacityPerProductionLine = monthlyCapacityPerProductionLine;
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
