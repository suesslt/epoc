package com.jore.epoc;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.jore.datatypes.currency.Currency;
import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.epoc.bo.Company;
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
import com.jore.epoc.bo.orders.CreditEventDirection;
import com.jore.epoc.bo.orders.EnterMarketOrder;
import com.jore.epoc.bo.orders.IncreaseProductivityOrder;
import com.jore.epoc.bo.orders.IncreaseQualityOrder;
import com.jore.epoc.bo.orders.MarketingCampaignOrder;
import com.jore.epoc.bo.settings.EpocSettings;

public class SimulationBuilder {
    private static final String CHF = "CHF";
    private static int ID = 0;

    public static SimulationBuilder builder() {
        return new SimulationBuilder();
    }

    // System parameters
    private Percent interestRate = Percent.of("5%");
    private Currency baseCurrency = Currency.getInstance(CHF);
    private Money constructionCostsPerUnit = Money.of(CHF, 100);
    private Money constructionCosts = Money.of(CHF, 1000000);
    private Money rawMaterialUnitPrice = Money.of(CHF, 300);
    private Money factoryConstructionCosts = Money.of(CHF, 1000000);
    private Percent demandCurveLowerPercent = Percent.of("80%");
    private Money demandCurveLowerPrice = Money.of(CHF, 500);
    private Percent demandCurveHigherPercent = Percent.of("20%");
    private Money demandCurveHigherPrice = Money.of(CHF, 1200);
    private int productLifecycleDuration = 100;
    private Integer timeToBuildStorage = 0;
    private Integer timeToBuildFactory = 0;
    private Integer monthlyCapacityPerProductionLine = 100;
    private Money factoryLaborCost = Money.of(CHF, 500000);
    private String marketName = "Switzerland";
    private Money marketDistributionCost = Money.of(CHF, 2000000);
    private int marketSize = 1000000;
    private Money buildingMaintenanceCost = Money.of(CHF, 10000);
    private Percent depreciationRate = Percent.of("15%");
    private Money headquarterCost = Money.of(CHF, 1500000);
    private Money inventoryManagementCost = Money.of(CHF, 500000);
    private Money marketEntryCost = Money.of(CHF, 400000);
    private Money productionCost = Money.of(CHF, 30);
    private Integer simulationPassiveSteps = 2;
    private Money pricePerPointQuality = Money.of("CHF", 200000);
    private Money pricePerMarketingCampaign = Money.of("CHF", 500000);
    private Money pricePerProductivityPoint = Money.of("CHF", 500000);
    private Percent factorDiscountRate = Percent.of("10%");
    // To be set in application
    private Money initialOfferedPrice = Money.of(CHF, 800);
    private int initialIntendedSale = 1000;
    private Integer productionLines = 10;
    private Money productionLineConstructionCosts = Money.of(CHF, 100000);
    private YearMonth simulationStart = YearMonth.of(2020, 1);
    private String name = "Unnamed Company";
    private String simulationName = "Unnamed Simulation";
    private Integer numberOfSimulationSteps = 24;
    private List<AbstractSimulationOrder> orders = new ArrayList<>();
    private Simulation simulation;

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

    public SimulationBuilder buildFactory(YearMonth executionMonth) {
        BuildFactoryOrder order = new BuildFactoryOrder();
        order.setExecutionMonth(executionMonth);
        order.setConstructionCost(factoryConstructionCosts);
        order.setConstructionCostPerLine(productionLineConstructionCosts);
        order.setProductionLines(productionLines);
        order.setTimeToBuild(timeToBuildFactory);
        order.setMonthlyCapacityPerProductionLine(monthlyCapacityPerProductionLine);
        order.setProductionLineLaborCost(factoryLaborCost);
        orders.add(order);
        return this;
    }

    public SimulationBuilder buildStorage(YearMonth executionMonth, int storageCapacity) {
        BuildStorageOrder order = new BuildStorageOrder();
        order.setExecutionMonth(executionMonth);
        order.setCapacity(storageCapacity);
        order.setConstructionCostPerUnit(constructionCostsPerUnit);
        order.setConstructionCost(constructionCosts);
        order.setTimeToBuild(timeToBuildStorage);
        order.setInventoryManagementCost(inventoryManagementCost);
        orders.add(order);
        return this;
    }

    public SimulationBuilder buyRawMaterial(YearMonth executionMonth, int amount) {
        BuyRawMaterialOrder order = new BuyRawMaterialOrder();
        order.setExecutionMonth(executionMonth);
        order.setAmount(amount);
        order.setUnitPrice(rawMaterialUnitPrice);
        orders.add(order);
        return this;
    }

    public SimulationBuilder changeAmountAndPriceOrder(YearMonth executionMonth, int amount, Money price) {
        ChangeAmountAndPriceOrder order = new ChangeAmountAndPriceOrder();
        order.setExecutionMonth(executionMonth);
        order.setIntentedSales(amount);
        order.setOfferedPrice(price);
        orders.add(order);
        return this;
    }

    public SimulationBuilder enterMarket(YearMonth executionMonth) {
        EnterMarketOrder order = new EnterMarketOrder();
        order.setExecutionMonth(executionMonth);
        order.setOfferedPrice(initialOfferedPrice);
        order.setIntentedProductSale(initialIntendedSale);
        order.setEnterMarktCost(marketEntryCost);
        orders.add(order);
        return this;
    }

    public SimulationBuilder increaseCreditLine(YearMonth executionMonth, Money increaseAmount) {
        AdjustCreditLineOrder order = new AdjustCreditLineOrder();
        order.setExecutionMonth(executionMonth);
        order.setAmount(increaseAmount);
        order.setDirection(CreditEventDirection.INCREASE);
        order.setInterestRate(interestRate);
        orders.add(order);
        return this;
    }

    public SimulationBuilder increaseProductivity(YearMonth executionMonth, Money increaseProductivityAmount) {
        IncreaseProductivityOrder order = new IncreaseProductivityOrder();
        order.setExecutionMonth(executionMonth);
        order.setAmount(increaseProductivityAmount);
        orders.add(order);
        return this;
    }

    public SimulationBuilder increaseQuality(YearMonth executionMonth, Money increaseQualityAmount) {
        IncreaseQualityOrder order = new IncreaseQualityOrder();
        order.setExecutionMonth(executionMonth);
        order.setAmount(increaseQualityAmount);
        orders.add(order);
        return this;
    }

    public SimulationBuilder laborForce(int marketSize) {
        this.marketSize = marketSize;
        return this;
    }

    public SimulationBuilder marketingCampaign(YearMonth executionMonth, Money marketingCampaignAmount) {
        MarketingCampaignOrder order = new MarketingCampaignOrder();
        order.setExecutionMonth(executionMonth);
        order.setAmount(marketingCampaignAmount);
        orders.add(order);
        return this;
    }

    public SimulationBuilder marketSize(int marketSize) {
        this.marketSize = marketSize;
        return this;
    }

    public SimulationBuilder name(String name) {
        this.name = name;
        return this;
    }

    public SimulationBuilder numberOfSimulationSteps(Integer numberOfSimulationSteps) {
        this.numberOfSimulationSteps = numberOfSimulationSteps;
        return this;
    }

    public SimulationBuilder passiveSteps(int passiveSteps) {
        this.simulationPassiveSteps = passiveSteps;
        return this;
    }

    public SimulationBuilder simulation(Simulation simulation) {
        this.simulation = simulation;
        return this;
    }

    public SimulationBuilder simulationName(String simulationName) {
        this.simulationName = simulationName;
        return this;
    }

    private Market createMarket() {
        Market market = new Market();
        market.setId(ID++);
        market.setName(marketName);
        market.setMarketSize(marketSize);
        market.setDistributionCost(marketDistributionCost);
        market.setCostToEnterMarket(marketEntryCost);
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
        EpocSettings settings = new EpocSettings();
        simulation = new Simulation();
        settings.addSetting(SettingBuilder.builder().settingKey(EpocSettings.PASSIVE_STEPS).valueText(simulationPassiveSteps.toString()).build());
        settings.addSetting(SettingBuilder.builder().settingKey(EpocSettings.PRICE_PER_POINT_QUALITY).valueText(pricePerPointQuality.toString()).build());
        settings.addSetting(SettingBuilder.builder().settingKey(EpocSettings.PRICE_PER_MARKETING_CAMPAIGN).valueText(pricePerMarketingCampaign.toString()).build());
        settings.addSetting(SettingBuilder.builder().settingKey(EpocSettings.PRICE_PER_PRODUCTIVITY_POINT).valueText(pricePerProductivityPoint.toString()).build());
        settings.addSetting(SettingBuilder.builder().settingKey(EpocSettings.FACTOR_DISCOUNT_RATE).valueText(factorDiscountRate.toString()).build());
        simulation.setSettings(settings);
        simulation.setId(ID++);
        simulation.setName(simulationName);
        simulation.setStartMonth(simulationStart);
        simulation.setNrOfMonths(numberOfSimulationSteps);
        simulation.setInterestRate(interestRate);
        simulation.setBuildingMaintenanceCost(buildingMaintenanceCost);
        simulation.setDepreciationRate(depreciationRate);
        simulation.setHeadquarterCost(headquarterCost);
        simulation.setProductionCost(productionCost);
    }
}
