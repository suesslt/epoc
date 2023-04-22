package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.Company.MonthlySale;
import com.jore.epoc.bo.CompanySimulationStep;
import com.jore.epoc.bo.DistributionInMarket;
import com.jore.epoc.bo.DistributionStep;
import com.jore.epoc.bo.Factory;
import com.jore.epoc.bo.Market;
import com.jore.epoc.bo.MarketSimulation;
import com.jore.epoc.bo.Simulation;
import com.jore.epoc.bo.SimulationStep;
import com.jore.epoc.bo.Storage;

import lombok.extern.log4j.Log4j2;

@Log4j2
class MarketSimulationTests {
    private static final int LABOR_FORCE = 1000000;
    private static final String CHF = "CHF";

    @Test
    public void test() {
        MarketSimulation marketSimulation = createMarketSimulation();
        Simulation simulation = createSimulation(12);
        simulation.addMarketSimulation(marketSimulation);
        addCompaniesToSimulation(marketSimulation, simulation);
        addStepToSimulation(simulation, YearMonth.of(2020, 1), Money.of(CHF, 50), 10000, 1);
        addStepToSimulation(simulation, YearMonth.of(2020, 2), Money.of(CHF, 50), 10000, 2);
        addStepToSimulation(simulation, YearMonth.of(2020, 3), Money.of(CHF, 50), 10000, 4);
        addStepToSimulation(simulation, YearMonth.of(2020, 4), Money.of(CHF, 50), 10000, 8);
        addStepToSimulation(simulation, YearMonth.of(2020, 5), Money.of(CHF, 50), 10000, 16);
        addStepToSimulation(simulation, YearMonth.of(2020, 6), Money.of(CHF, 50), 10000, 0);
        assertEquals(600000, marketSimulation.calculateMarketPotentialForProductPrice(1000000, Money.of(CHF, 40)));
        assertEquals(93, marketSimulation.calculateProductsSold());
        marketSimulation.simulateMarket(YearMonth.of(2020, 6));
        assertEquals(8775, marketSimulation.calculateProductsSold());
    }

    @Test
    public void testFullDistribution() {
        Simulation simulation = createSimulation(100);
        MarketSimulation marketSimulation = createMarketSimulation();
        simulation.addMarketSimulation(marketSimulation);
        addCompaniesToSimulation(marketSimulation, simulation);
        Optional<SimulationStep> activeSimulationStep = simulation.getActiveSimulationStep();
        while (activeSimulationStep.isPresent()) {
            for (CompanySimulationStep companySimulationStep : activeSimulationStep.get().getCompanySimulationSteps()) {
                simulation.finishCompanyStep(companySimulationStep);
            }
            activeSimulationStep = simulation.getActiveSimulationStep();
        }
        assertEquals(999996, marketSimulation.getSoldProducts());
        Company company = simulation.getCompanies().get(0);
        List<MonthlySale> soldProductsPerMonth = company.getSoldProductsPerMonth();
        log.info(soldProductsPerMonth);
        log.info(company.getPnL());
    }

    private void addCompaniesToSimulation(MarketSimulation marketSimulation, Simulation simulation) {
        simulation.addCompany(createCompany("Company A", marketSimulation));
        simulation.addCompany(createCompany("Company B", marketSimulation));
        simulation.addCompany(createCompany("Company C", marketSimulation));
    }

    private void addStepToSimulation(Simulation simulation, YearMonth simulationMonth, Money offeredPrice, int intentedProductSale, int soldProducts) {
        SimulationStep simulationStep = new SimulationStep();
        simulationStep.setOpen(false);
        simulationStep.setSimulationMonth(simulationMonth);
        for (Company company : simulation.getCompanies()) {
            CompanySimulationStep companySimulationStep = new CompanySimulationStep();
            companySimulationStep.setOpen(false);
            simulationStep.addCompanySimulationStep(companySimulationStep);
            company.addCompanySimulationStep(companySimulationStep);
            for (DistributionInMarket distributionInMarket : company.getDistributionInMarkets()) {
                DistributionStep distributionStep = new DistributionStep();
                distributionStep.setOfferedPrice(offeredPrice);
                distributionStep.setIntentedProductSale(intentedProductSale);
                distributionStep.setSoldProducts(soldProducts);
                companySimulationStep.addDistributionStep(distributionStep);
                distributionInMarket.addDistributionStep(distributionStep);
            }
        }
        simulation.addSimulationStep(simulationStep);
    }

    private Company createCompany(String name, MarketSimulation marketSimulation) {
        Company result = new Company();
        result.setName(name);
        Storage storage = new Storage();
        storage.setCapacity(1000000);
        storage.setStoredProducts(1000000);
        storage.setStorageCostPerUnitAndMonth(Money.of("CHF", 1));
        result.addStorage(storage);
        Factory factory = new Factory();
        factory.setMonthlyCapacityPerProductionLine(1000);
        factory.setProductionLines(10);
        factory.setProductionStartMonth(YearMonth.of(2020, 1));
        factory.setUnitLabourCost(Money.of("CHF", 1));
        factory.setUnitProductionCost(Money.of("CHF", 1));
        result.addFactory(factory);
        DistributionInMarket distributionInMarket = new DistributionInMarket();
        distributionInMarket.setIntentedProductSale(100000);
        distributionInMarket.setOfferedPrice(Money.of("CHF", 80));
        result.addDistributionInMarket(distributionInMarket);
        marketSimulation.addDistributionInMarket(distributionInMarket);
        return result;
    }

    private Market createMarket() {
        Market result = new Market();
        result.setName("Europe");
        result.setLaborForce(LABOR_FORCE);
        return result;
    }

    private MarketSimulation createMarketSimulation() {
        MarketSimulation marketSimulation = new MarketSimulation();
        marketSimulation.setStartMonth(YearMonth.of(2020, 1));
        marketSimulation.setLowerPrice(Money.of(CHF, 20));
        marketSimulation.setLowerPercent(Percent.of(0.8d));
        marketSimulation.setHigherPrice(Money.of(CHF, 80));
        marketSimulation.setHigherPercent(Percent.of(0.2d));
        marketSimulation.setProductLifecycleDuration(100);
        marketSimulation.setMarket(createMarket());
        return marketSimulation;
    }

    private Simulation createSimulation(int nrOfSteps) {
        Simulation result = new Simulation();
        result.setName("Test Simulation");
        result.setStarted(true);
        result.setFinished(false);
        result.setNrOfSteps(nrOfSteps);
        result.setStartMonth(YearMonth.of(2020, 1));
        return result;
    }
}
