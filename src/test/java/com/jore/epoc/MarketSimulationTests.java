package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.YearMonth;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.CompanySimulationStep;
import com.jore.epoc.bo.CreditEventDirection;
import com.jore.epoc.bo.Market;
import com.jore.epoc.bo.MarketSimulation;
import com.jore.epoc.bo.Simulation;
import com.jore.epoc.bo.SimulationStep;
import com.jore.epoc.bo.orders.AdjustCreditLineOrder;
import com.jore.epoc.bo.orders.BuildFactoryOrder;
import com.jore.epoc.bo.orders.BuildStorageOrder;
import com.jore.epoc.bo.orders.BuyRawMaterialOrder;
import com.jore.epoc.bo.orders.EnterMarketOrder;

class MarketSimulationTests {
    private static final Money STORAGE_COST_PER_UNIT_AND_MONTH = Money.of("CHF", 1);
    private static final int MONTHLY_CAPACITY_PER_PRODUCTION_LINE = 1000;
    private static final Money FACTORY_COST_PER_PRODUCTION_LINE = Money.of("CHF", 10);
    private static final Money FACTORY_FIXED_COSTS = Money.of("CHF", 1000);
    private static final int FACTORY_TIME_TO_BUILD = 0;
    private static final Percent CREDIT_LINE_INTEREST_RATE = Percent.of("5%");
    private static final int MARKET_SIZE = 100000;
    private static final String CHF = "CHF";
    private static final Money FACTORY_UNIT_LABOR_COST = Money.of("CHF", 1);
    private static final Money FACTORY_UNIT_PRODUCTION_COST = Money.of("CHF", 1);
    private static final int STORAGE_TIME_TO_BUILD = 0;
    private static final Money RAW_MATERIAL_UNIT_PRICE = Money.of("CHF", 1);
    private int id = 1;

    @Test
    public void testSoldProductsFullDistributionForOneCompany() {
        Simulation simulation = createSimulation(100, YearMonth.of(2020, 1));
        Market market = createMarket();
        MarketSimulation marketSimulation = createMarketSimulation(market);
        simulation.addMarketSimulation(marketSimulation);
        Company companyA = createCompany("Company A");
        simulation.addCompany(companyA);
        addAdjustCreditLineOrderToCompany(companyA, YearMonth.of(2020, 1), Money.of(CHF, 1000000000));
        addBuildStorageOrderToCompany(companyA, YearMonth.of(2020, 1), 1000000, 0);
        addBuyRawMaterialOrderToCompany(companyA, YearMonth.of(2020, 1), 1000000);
        addBuildFactoryOrderToCompany(companyA, YearMonth.of(2020, 1), 10);
        addEnterMarketOrderToCompany(companyA, YearMonth.of(2020, 1), marketSimulation, 1000, Money.of(CHF, 50));
        Optional<SimulationStep> activeSimulationStep = simulation.getActiveSimulationStep();
        while (activeSimulationStep.isPresent()) {
            for (CompanySimulationStep companySimulationStep : activeSimulationStep.get().getCompanySimulationSteps()) {
                simulation.finishCompanyStep(companySimulationStep);
            }
            activeSimulationStep = simulation.getActiveSimulationStep();
        }
        assertEquals(96407, marketSimulation.getSoldProducts());
        assertEquals(96407, companyA.getSoldProducts());
    }

    @Test
    public void testSoldProductsFullDistributionForTwoCompanies() {
        Simulation simulation = createSimulation(100, YearMonth.of(2020, 1));
        Market market = createMarket();
        MarketSimulation marketSimulation = createMarketSimulation(market);
        simulation.addMarketSimulation(marketSimulation);
        Company companyA = createCompany("Company A");
        simulation.addCompany(companyA);
        Company companyB = createCompany("Company B");
        simulation.addCompany(companyB);
        addAdjustCreditLineOrderToCompany(companyA, YearMonth.of(2020, 1), Money.of(CHF, 10000000));
        addBuildStorageOrderToCompany(companyA, YearMonth.of(2020, 1), 100000, 0);
        addBuyRawMaterialOrderToCompany(companyA, YearMonth.of(2020, 1), 1000000);
        addBuildFactoryOrderToCompany(companyA, YearMonth.of(2020, 1), 10);
        addEnterMarketOrderToCompany(companyA, YearMonth.of(2020, 1), marketSimulation, 1000, Money.of(CHF, 50));
        addAdjustCreditLineOrderToCompany(companyB, YearMonth.of(2020, 1), Money.of(CHF, 10000000));
        addBuildStorageOrderToCompany(companyB, YearMonth.of(2020, 1), 100000, 0);
        addBuyRawMaterialOrderToCompany(companyB, YearMonth.of(2020, 1), 1000000);
        addBuildFactoryOrderToCompany(companyB, YearMonth.of(2020, 1), 10);
        addEnterMarketOrderToCompany(companyB, YearMonth.of(2020, 1), marketSimulation, 1000, Money.of(CHF, 50));
        Optional<SimulationStep> activeSimulationStep = simulation.getActiveSimulationStep();
        while (activeSimulationStep.isPresent()) {
            for (CompanySimulationStep companySimulationStep : activeSimulationStep.get().getCompanySimulationSteps()) {
                simulation.finishCompanyStep(companySimulationStep);
            }
            activeSimulationStep = simulation.getActiveSimulationStep();
        }
        assertEquals(99998, marketSimulation.getSoldProducts());
        assertEquals(49999, companyA.getSoldProducts());
        assertEquals(49999, companyB.getSoldProducts());
    }

    private void addAdjustCreditLineOrderToCompany(Company company, YearMonth executionMonth, Money amount) {
        AdjustCreditLineOrder order = new AdjustCreditLineOrder();
        order.setId(id++);
        order.setExecutionMonth(executionMonth);
        order.setDirection(CreditEventDirection.INCREASE);
        order.setAdjustAmount(amount);
        order.setInterestRate(CREDIT_LINE_INTEREST_RATE);
        company.addSimulationOrder(order);
    }

    private void addBuildFactoryOrderToCompany(Company company, YearMonth executionMonth, Integer productionLines) {
        BuildFactoryOrder order = new BuildFactoryOrder();
        order.setId(id++);
        order.setExecutionMonth(executionMonth);
        order.setConstructionCosts(FACTORY_FIXED_COSTS);
        order.setConstructionCostsPerLine(FACTORY_COST_PER_PRODUCTION_LINE);
        order.setMonthlyCapacityPerProductionLine(MONTHLY_CAPACITY_PER_PRODUCTION_LINE);
        order.setProductionLines(productionLines);
        order.setUnitLabourCost(FACTORY_UNIT_LABOR_COST);
        order.setUnitProductionCost(FACTORY_UNIT_PRODUCTION_COST);
        order.setTimeToBuild(FACTORY_TIME_TO_BUILD);
        company.addSimulationOrder(order);
    }

    private void addBuildStorageOrderToCompany(Company company, YearMonth executionMonth, Integer capacity, int buildTime) {
        BuildStorageOrder order = new BuildStorageOrder();
        order.setId(id++);
        order.setExecutionMonth(executionMonth);
        order.setConstructionCosts(FACTORY_FIXED_COSTS);
        order.setConstructionCostsPerUnit(FACTORY_COST_PER_PRODUCTION_LINE);
        order.setCapacity(capacity);
        order.setStorageCostPerUnitAndMonth(STORAGE_COST_PER_UNIT_AND_MONTH);
        order.setTimeToBuild(STORAGE_TIME_TO_BUILD);
        company.addSimulationOrder(order);
    }

    private void addBuyRawMaterialOrderToCompany(Company company, YearMonth executionMonth, Integer amount) {
        BuyRawMaterialOrder order = new BuyRawMaterialOrder();
        order.setId(id++);
        order.setExecutionMonth(executionMonth);
        order.setAmount(amount);
        order.setUnitPrice(RAW_MATERIAL_UNIT_PRICE);
        company.addSimulationOrder(order);
    }

    private void addEnterMarketOrderToCompany(Company company, YearMonth executionMonth, MarketSimulation marketSimulation, int intendedProductSale, Money offeredPrice) {
        EnterMarketOrder order = new EnterMarketOrder();
        order.setId(id++);
        order.setExecutionMonth(executionMonth);
        order.setIntentedProductSale(intendedProductSale);
        order.setOfferedPrice(offeredPrice);
        order.setMarketSimulation(marketSimulation);
        company.addSimulationOrder(order);
    }

    private Company createCompany(String name) {
        Company result = new Company();
        result.setId(id++);
        result.setName(name);
        return result;
    }

    private Market createMarket() {
        Market result = new Market();
        result.setId(id++);
        result.setName("Europe");
        result.setLaborForce(MARKET_SIZE);
        return result;
    }

    private MarketSimulation createMarketSimulation(Market market) {
        MarketSimulation result = new MarketSimulation();
        result.setId(id++);
        result.setStartMonth(YearMonth.of(2020, 1));
        result.setLowerPrice(Money.of(CHF, 20));
        result.setLowerPercent(Percent.of(0.8d));
        result.setHigherPrice(Money.of(CHF, 80));
        result.setHigherPercent(Percent.of(0.2d));
        result.setProductLifecycleDuration(100);
        result.setMarket(market);
        return result;
    }

    private Simulation createSimulation(int nrOfSteps, YearMonth startMonth) {
        Simulation result = new Simulation();
        result.setId(id++);
        result.setName("Test Simulation");
        result.setStarted(true);
        result.setFinished(false);
        result.setNrOfSteps(nrOfSteps);
        result.setStartMonth(startMonth);
        return result;
    }
}
