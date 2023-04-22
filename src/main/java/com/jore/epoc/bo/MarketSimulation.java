package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CompositeType;
import org.hibernate.annotations.Type;

import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.jpa.BusinessObject;
import com.jore.util.Util;

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
//@Log4j2
/**
 * marketSize = Nr of potential sales in market
 * productsSold = Nr of Products already sold
 * availableMarketSize = marketSize - productsSold
 * availableMarketPotential = Potential for customer or product, depending on price
 *
 *
 * Vorgehen beim Berechnen der Verkaufsmengen:
 *
 * 1. Festlegen der Marktgrösse für einen Produktetyp. Diese hängt je nach Produkt von der Kaufkraft
 * und der demographischen Verteilung eines Marktes oder Marktsegmentes ab.
 * 2. Festlegen des Marktpotentials pro spezifischem Produkt. Das Marktpotential hängt insbesondere vom Preis ab
 * (Angebots- und Nachfragekurve) sowie von der Qualität des Produtes.
 * 3. Da die Summe der Marktpotentiale potentiell grösser ist als die Marktgrösse werden die Marktpotentiale
 * proportional angepasst.
 * 4. Der zeitliche Verlauf der Menge der verkauften Produkte folgt einer Gaussschen Kurve. Obwohl die Produkte
 * verschiedener Anbieter einander beeinflussen, wird zwecks Einfachheit und Verständlichkeit pro Produkt
 * eine eigene Kurve verfolgt.
 *
 * Wird die verfügbare Marktgrösse oder die absolute Marktgrösse angepasst?
 *
 * Der Begriff des Marktanteils muss eingeführt werden
 *
 */
public class MarketSimulation extends BusinessObject {
    @ManyToOne(optional = false)
    private Market market;
    @ManyToOne(optional = false)
    private Simulation simulation;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "marketSimulation", orphanRemoval = true)
    private List<DistributionInMarket> distributionInMarkets = new ArrayList<>();
    private YearMonth startMonth;
    @AttributeOverride(name = "amount", column = @Column(name = "higher_price_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "higher_price_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money higherPrice;
    @Type(com.jore.datatypes.hibernate.PercentUserType.class)
    private Percent higherPercent;
    @AttributeOverride(name = "amount", column = @Column(name = "lower_price_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "lower_price_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money lowerPrice;
    @Type(com.jore.datatypes.hibernate.PercentUserType.class)
    private Percent lowerPercent;
    private int productLifecycleDuration;

    public void addDistributionInMarket(DistributionInMarket distributionInMarket) {
        distributionInMarket.setMarketSimulation(this);
        distributionInMarkets.add(distributionInMarket);
    }

    public int calculateMarketPotentialForProductPrice(int marketSize, Money offeredPrice) {
        DemandCurve demandCurve = new DemandCurve(higherPrice, higherPercent, lowerPrice, lowerPercent);
        return demandCurve.getDemandForPrice(offeredPrice).applyTo(marketSize);
    }

    public int calculateProductsSold() {
        return distributionInMarkets.stream().mapToInt(distribution -> distribution.getSoldProducts()).sum();
    }

    public Integer getSoldProducts() {
        return distributionInMarkets.stream().mapToInt(distribution -> distribution.getSoldProducts()).sum();
    }

    public void simulateMarket(YearMonth simulationMonth) {
        int marketSize = market.getMarketSizeForConsumption();
        int productsSold = calculateProductsSold();
        int availableMarketSize = marketSize - productsSold;
        for (DistributionInMarket distributionInMarket : distributionInMarkets) {
            addDistributionStep(distributionInMarket, simulationMonth);
            int marketPotentialForProduct = calculateMarketPotentialForProductPrice(marketSize, distributionInMarket.getOfferedPrice(simulationMonth));
            distributionInMarket.setMarketPotentialForProduct(simulationMonth, marketPotentialForProduct);
        }
        int totalMarketPotential = distributionInMarkets.stream().mapToInt(distribution -> distribution.getMarketPotentialForProduct(simulationMonth)).sum();
        for (DistributionInMarket distributionInMarket : distributionInMarkets) {
            int marketPotentialForProduct = distributionInMarket.getMarketPotentialForProduct(simulationMonth);
            int availableMarketPotentialForProduct = (int) ((double) marketPotentialForProduct / (double) totalMarketPotential * availableMarketSize);
            double percentageSold = new ProductLifecycle(productLifecycleDuration).getPercentageSoldForMonths(Util.monthDiff(simulationMonth, startMonth));
            int maximumToSell = (int) (availableMarketPotentialForProduct * percentageSold);
            distributionInMarket.getCompany().sellMaximumOf(distributionInMarket, simulationMonth, maximumToSell, distributionInMarket.getOfferedPrice(simulationMonth));
        }
    }

    private void addDistributionStep(DistributionInMarket distributionInMarket, YearMonth simulationMonth) {
        DistributionStep distributionStep = new DistributionStep();
        distributionStep.setOfferedPrice(distributionInMarket.getOfferedPrice());
        distributionStep.setIntentedProductSale(distributionInMarket.getIntentedProductSale());
        getCompanySimulationStepForMonth(distributionInMarket, simulationMonth).addDistributionStep(distributionStep);
        distributionInMarket.addDistributionStep(distributionStep);
    }

    private CompanySimulationStep getCompanySimulationStepForMonth(DistributionInMarket distributionInMarket, YearMonth simulationMonth) {
        return distributionInMarket.getCompany().getCompanySimulationSteps().stream().filter(step -> step.getSimulationStep().getSimulationMonth().equals(simulationMonth)).findFirst().get();
    }
}
