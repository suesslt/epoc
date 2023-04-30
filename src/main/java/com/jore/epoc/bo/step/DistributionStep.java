package com.jore.epoc.bo.step;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.DistributionInMarket;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
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

    public CompanySimulationStep getCompanySimulationStep() {
        return companySimulationStep;
    }

    public DistributionInMarket getDistributionInMarket() {
        return distributionInMarket;
    }

    public int getIntentedProductSale() {
        return intentedProductSale;
    }

    public int getMarketPotentialForProduct() {
        return marketPotentialForProduct;
    }

    public Money getOfferedPrice() {
        return offeredPrice;
    }

    public int getSoldProducts() {
        return soldProducts;
    }

    public void setCompanySimulationStep(CompanySimulationStep companySimulationStep) {
        this.companySimulationStep = companySimulationStep;
    }

    public void setDistributionInMarket(DistributionInMarket distributionInMarket) {
        this.distributionInMarket = distributionInMarket;
    }

    public void setIntentedProductSale(int intentedProductSale) {
        this.intentedProductSale = intentedProductSale;
    }

    public void setMarketPotentialForProduct(int marketPotentialForProduct) {
        this.marketPotentialForProduct = marketPotentialForProduct;
    }

    public void setOfferedPrice(Money offeredPrice) {
        this.offeredPrice = offeredPrice;
    }

    public void setSoldProducts(int soldProducts) {
        this.soldProducts = soldProducts;
    }
}
