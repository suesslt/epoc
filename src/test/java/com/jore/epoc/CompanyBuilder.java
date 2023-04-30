package com.jore.epoc;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.jore.datatypes.currency.Currency;
import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.CreditEventDirection;
import com.jore.epoc.bo.Market;
import com.jore.epoc.bo.MarketSimulation;
import com.jore.epoc.bo.Simulation;
import com.jore.epoc.bo.accounting.FinancialAccounting;
import com.jore.epoc.bo.orders.AbstractSimulationOrder;
import com.jore.epoc.bo.orders.AdjustCreditLineOrder;
import com.jore.epoc.bo.orders.BuildFactoryOrder;
import com.jore.epoc.bo.orders.BuildStorageOrder;
import com.jore.epoc.bo.orders.BuyRawMaterialOrder;
import com.jore.epoc.bo.orders.ChangeAmountAndPriceOrder;
import com.jore.epoc.bo.orders.EnterMarketOrder;

public class CompanyBuilder {
    private static final String CHF = "CHF";
    private static int ID = 0;

    public static CompanyBuilder builder() {
        return new CompanyBuilder();
    }

    // System parameters
    private Percent interestRate = Percent.of("5%");
    private Currency baseCurrency = Currency.getInstance(CHF);
    private Money constructionCostsPerUnit = Money.of(CHF, 100);
    private Money constructionCosts = Money.of(CHF, 1000000);
    private Money rawMaterialUnitPrice = Money.of(CHF, 10);
    private Money factoryConstructionCosts = Money.of(CHF, 1000000);
    private Percent demandCurveLowerPercent = Percent.of("80%");
    private Money demandCurveLowerPrice = Money.of(CHF, 20);
    private Percent demandCurveHigherPercent = Percent.of("20%");
    private Money demandCurveHigherPrice = Money.of(CHF, 80);
    private int productLifecycleDuration = 100;
    private Integer timeToBuildStorage = 0;
    private Integer timeToBuildFactory = 0;
    private Integer monthlyCapacityPerProductionLine = 100;
    private Money unitProductionCost = Money.of(CHF, 10);
    private Money factoryLaborCost = Money.of(CHF, 500000);
    private String marketName = "Switzerland";
    private Money marketDistributionCost = Money.of(CHF, 2000000);
    private int marketLaborForce = 1000000;
    private Money buildingMaintenanceCost = Money.of(CHF, 10000);
    private Percent depreciationRate = Percent.of("15%");
    private Money headquarterCost = Money.of(CHF, 1500000);
    private Money inventoryManagementCost = Money.of(CHF, 500000);
    // To be set in application
    private Money initialOfferedPrice = Money.of(CHF, 50);
    private int initialIntendedSale = 1000;
    private Integer productionLines = 10;
    private Money productionLineConstructionCosts = Money.of(CHF, 100000);
    private YearMonth simulationStart = YearMonth.of(2020, 1);
    private String name = "Unnamed Company";
    private String simulationName = "Unnamed Simulation";
    private Integer numberOfSimulationSteps = 24;
    private List<AbstractSimulationOrder> orders = new ArrayList<>();
    private Simulation simulation;

    public CompanyBuilder baseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
        return this;
    }

    public Company build() {
        Company result = new Company();
        result.setId(ID++);
        result.setName(name);
        if (simulation == null) {
            createSimulation();
            Market market = createMarket();
            MarketSimulation marketSimulation = createMarketSimulation();
            marketSimulation.setMarket(market);
            simulation.addMarketSimulation(marketSimulation);
        }
        simulation.addCompany(result);
        FinancialAccounting accounting = new FinancialAccounting();
        accounting.setId(ID++);
        accounting.setBaseCurrency(baseCurrency);
        result.setFinancialAccounting(accounting);
        for (AbstractSimulationOrder order : orders) {
            result.addSimulationOrder(order);
            if (order instanceof EnterMarketOrder enterMarketOrder) {
                enterMarketOrder.setMarketSimulation(simulation.getMarketSimulations().get(0));
            }
            if (order instanceof ChangeAmountAndPriceOrder changeAmountAndPriceOrder) {
                changeAmountAndPriceOrder.setMarket(simulation.getMarketSimulations().get(0).getMarket());
            }
        }
        return result;
    }

    public CompanyBuilder buildFactory(YearMonth executionMonth) {
        BuildFactoryOrder order = new BuildFactoryOrder();
        order.setExecutionMonth(executionMonth);
        order.setConstructionCosts(factoryConstructionCosts);
        order.setConstructionCostsPerLine(productionLineConstructionCosts);
        order.setProductionLines(productionLines);
        order.setTimeToBuild(timeToBuildFactory);
        order.setMonthlyCapacityPerProductionLine(monthlyCapacityPerProductionLine);
        order.setProductionLineLaborCost(factoryLaborCost);
        order.setUnitProductionCost(unitProductionCost);
        orders.add(order);
        return this;
    }

    public CompanyBuilder buildStorage(YearMonth executionMonth, int storageCapacity) {
        BuildStorageOrder order = new BuildStorageOrder();
        order.setExecutionMonth(executionMonth);
        order.setCapacity(storageCapacity);
        order.setConstructionCostsPerUnit(constructionCostsPerUnit);
        order.setConstructionCosts(constructionCosts);
        order.setTimeToBuild(timeToBuildStorage);
        order.setInventoryManagementCost(inventoryManagementCost);
        orders.add(order);
        return this;
    }

    public CompanyBuilder buyRawMaterial(YearMonth executionMonth, int amount) {
        BuyRawMaterialOrder order = new BuyRawMaterialOrder();
        order.setExecutionMonth(executionMonth);
        order.setAmount(amount);
        order.setUnitPrice(rawMaterialUnitPrice);
        orders.add(order);
        return this;
    }

    public CompanyBuilder changeAmountAndPriceOrder(YearMonth executionMonth, int amount, Money price) {
        ChangeAmountAndPriceOrder order = new ChangeAmountAndPriceOrder();
        order.setExecutionMonth(executionMonth);
        order.setIntentedSales(amount);
        order.setOfferedPrice(price);
        orders.add(order);
        return this;
    }

    public CompanyBuilder enterMarket(YearMonth executionMonth) {
        EnterMarketOrder order = new EnterMarketOrder();
        order.setExecutionMonth(executionMonth);
        order.setOfferedPrice(initialOfferedPrice);
        order.setIntentedProductSale(initialIntendedSale);
        orders.add(order);
        return this;
    }

    public CompanyBuilder increaseCreditLine(YearMonth executionMonth, Money increaseAmount) {
        AdjustCreditLineOrder order = new AdjustCreditLineOrder();
        order.setExecutionMonth(executionMonth);
        order.setAmount(increaseAmount);
        order.setDirection(CreditEventDirection.INCREASE);
        order.setInterestRate(interestRate);
        orders.add(order);
        return this;
    }

    public CompanyBuilder laborForce(int laborForce) {
        this.marketLaborForce = laborForce;
        return this;
    }

    public CompanyBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CompanyBuilder numberOfSimulationSteps(Integer numberOfSimulationSteps) {
        this.numberOfSimulationSteps = numberOfSimulationSteps;
        return this;
    }

    public CompanyBuilder simulation(Simulation simulation) {
        this.simulation = simulation;
        return this;
    }

    public CompanyBuilder simulationName(String simulationName) {
        this.simulationName = simulationName;
        return this;
    }

    public CompanyBuilder simulationStart(YearMonth simulationStart) {
        this.simulationStart = simulationStart;
        return this;
    }

    private Market createMarket() {
        Market market = new Market();
        market.setId(ID++);
        market.setName(marketName);
        market.setLaborForce(marketLaborForce);
        market.setDistributionCost(marketDistributionCost);
        return market;
    }

    private MarketSimulation createMarketSimulation() {
        MarketSimulation marketSimulation = new MarketSimulation();
        marketSimulation.setId(ID++);
        marketSimulation.setStartMonth(simulationStart); // TODO has this really to be set manually?
        marketSimulation.setLowerPercent(demandCurveLowerPercent);
        marketSimulation.setLowerPrice(demandCurveLowerPrice);
        marketSimulation.setHigherPercent(demandCurveHigherPercent);
        marketSimulation.setHigherPrice(demandCurveHigherPrice);
        marketSimulation.setProductLifecycleDuration(productLifecycleDuration);
        return marketSimulation;
    }

    private void createSimulation() {
        simulation = new Simulation();
        simulation.setId(ID++);
        simulation.setName(simulationName);
        simulation.setStartMonth(simulationStart);
        simulation.setNrOfSteps(numberOfSimulationSteps);
        simulation.setInterestRate(interestRate);
        simulation.setBuildingMaintenanceCost(buildingMaintenanceCost);
        simulation.setDepreciationRate(depreciationRate);
        simulation.setHeadquarterCost(headquarterCost);
    }
}
