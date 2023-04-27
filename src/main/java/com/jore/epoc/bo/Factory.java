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

    public int produce(int maximumToProduce, YearMonth productionMonth) {
        Assert.isTrue("Capacity per production line must be greater zero.", monthlyCapacityPerProductionLine > 0);
        Assert.notNull("Production start month must not be null", productionStartMonth);
        Assert.notNull("Unit production costs must not be null", unitProductionCost);
        Assert.notNull("Unit labour costs must not be null", unitLabourCost);
        int result = isProductionReady(productionMonth) ? Math.min(maximumToProduce, productionLines * monthlyCapacityPerProductionLine) : 0;
        result = Math.min(result, company.getStorages().stream().mapToInt(storage -> storage.getStoredRawMaterials()).sum());
        if (result > 0) {
        }
        return result;
    }

    private boolean isProductionReady(YearMonth productionMonth) {
        return !productionMonth.isBefore(productionStartMonth);
    }
}
