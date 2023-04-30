package com.jore.epoc.bo;

import java.time.YearMonth;

import org.hibernate.annotations.CompositeType;

import com.jore.Assert;
import com.jore.datatypes.money.Money;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
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
    private Money productionLineLaborCost;

    public Company getCompany() {
        return company;
    }

    public int getMonthlyCapacityPerProductionLine() {
        return monthlyCapacityPerProductionLine;
    }

    public Money getProductionCost() {
        return productionLineLaborCost.multiply(productionLines);
    }

    public int getProductionLines() {
        return productionLines;
    }

    public YearMonth getProductionStartMonth() {
        return productionStartMonth;
    }

    public Money getUnitProductionCost() {
        return unitProductionCost;
    }

    public int produce(int maximumToProduce, YearMonth productionMonth) {
        Assert.isTrue("Capacity per production line must be greater zero.", monthlyCapacityPerProductionLine > 0);
        Assert.notNull("Production start month must not be null", productionStartMonth);
        Assert.notNull("Unit production costs must not be null", unitProductionCost);
        Assert.notNull("Production line labour costs must not be null", productionLineLaborCost);
        int result = isProductionReady(productionMonth) ? Math.min(maximumToProduce, productionLines * monthlyCapacityPerProductionLine) : 0;
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

    public void setUnitProductionCost(Money unitProductionCost) {
        this.unitProductionCost = unitProductionCost;
    }

    private boolean isProductionReady(YearMonth productionMonth) {
        return !productionMonth.isBefore(productionStartMonth);
    }
}
