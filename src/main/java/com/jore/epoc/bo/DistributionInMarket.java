package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
    private int intentedProductSale;

    public void addDistributionStep(DistributionStep distributionStep) {
        distributionStep.setDistributionInMarket(this);
        distributionSteps.add(distributionStep);
    }

    public int getIntentedProductSale(YearMonth simulationMonth) {
        return getDistributionStep(simulationMonth).getIntentedProductSale();
    }

    public int getMarketPotentialForProduct(YearMonth simulationMonth) {
        return getDistributionStep(simulationMonth).getMarketPotentialForProduct();
    }

    public Money getOfferedPrice(YearMonth simulationMonth) {
        return getDistributionStep(Objects.requireNonNull(simulationMonth)).getOfferedPrice();
    }

    public int getSoldProducts() {
        return distributionSteps.stream().mapToInt(distributionStep -> distributionStep.getSoldProducts()).sum();
    }

    public void setMarketPotentialForProduct(YearMonth simulationMonth, int marketPotentialForProduct) {
        getDistributionStep(simulationMonth).setMarketPotentialForProduct(marketPotentialForProduct);
    }

    public void setSoldProducts(YearMonth simulationMonth, int maximumToSell) {
        getDistributionStep(simulationMonth).setSoldProducts(maximumToSell);
    }

    private DistributionStep getDistributionStep(YearMonth simulationMonth) {
        return distributionSteps.stream().filter(step -> step.getCompanySimulationStep().getSimulationStep().getSimulationMonth().equals(simulationMonth)).findFirst().get();
    }
}
