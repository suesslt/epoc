package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.step.DistributionStep;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class DistributionInMarket extends BusinessObject {
    @ManyToOne(optional = false)
    private Company company;
    @ManyToOne(optional = false)
    private MarketSimulation marketSimulation;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "distributionInMarket", orphanRemoval = true)
    private List<DistributionStep> distributionSteps = new ArrayList<>();
    @AttributeOverride(name = "amount", column = @Column(name = "offered_price_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "offered_price_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money offeredPrice; // TODO rename, can be confused with method to get offered price from distribution step
    private Integer intentedProductSale;

    public void addDistributionStep(DistributionStep distributionStep) {
        distributionStep.setDistributionInMarket(this);
        distributionSteps.add(distributionStep);
    }

    public Company getCompany() {
        return company;
    }

    public Money getDistributionCost() {
        return getMarketSimulation().getMarket().getDistributionCost();
    }

    public Integer getIntentedProductSale() {
        return intentedProductSale;
    }

    public Integer getIntentedProductSale(YearMonth simulationMonth) {
        return getDistributionStep(simulationMonth).getIntentedProductSale();
    }

    public Integer getMarketPotentialForProduct(YearMonth simulationMonth) {
        return getDistributionStep(simulationMonth).getMarketPotentialForProduct();
    }

    public MarketSimulation getMarketSimulation() {
        return marketSimulation;
    }

    public Money getOfferedPrice() {
        return offeredPrice;
    }

    public Money getOfferedPrice(YearMonth simulationMonth) {
        return getDistributionStep(Objects.requireNonNull(simulationMonth)).getOfferedPrice();
    }

    public int getSoldProducts() {
        return distributionSteps.stream().mapToInt(distributionStep -> distributionStep.getSoldProducts()).sum();
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setIntentedProductSale(Integer intentedProductSale) {
        this.intentedProductSale = intentedProductSale;
    }

    public void setMarketPotentialForProduct(YearMonth simulationMonth, int marketPotentialForProduct) {
        getDistributionStep(simulationMonth).setMarketPotentialForProduct(marketPotentialForProduct);
    }

    public void setMarketSimulation(MarketSimulation marketSimulation) {
        this.marketSimulation = marketSimulation;
    }

    public void setOfferedPrice(Money offeredPrice) {
        this.offeredPrice = offeredPrice;
    }

    public void setSoldProducts(YearMonth simulationMonth, int maximumToSell) {
        getDistributionStep(simulationMonth).setSoldProducts(maximumToSell);
    }

    private DistributionStep getDistributionStep(YearMonth simulationMonth) {
        return distributionSteps.stream().filter(step -> step.getCompanySimulationStep().getSimulationStep().getSimulationMonth().equals(simulationMonth)).findFirst().get();
    }
}
