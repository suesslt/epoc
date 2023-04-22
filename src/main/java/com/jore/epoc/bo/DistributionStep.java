package com.jore.epoc.bo;

import org.hibernate.annotations.CompositeType;

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
public class DistributionStep extends BusinessObject {
    @ManyToOne(optional = false)
    private DistributionInMarket distributionInMarket;
    @ManyToOne(optional = false)
    private CompanySimulationStep companySimulationStep;
    private int soldProducts;
    private int intentedProductSale;
    @AttributeOverride(name = "amount", column = @Column(name = "offered_price_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "offered_price_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money offeredPrice;
    private int marketPotentialForProduct;
}
