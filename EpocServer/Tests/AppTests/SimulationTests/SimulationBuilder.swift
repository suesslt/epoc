import Foundation

/// Builder to construct complete simulation setups for testing.
/// Equivalent to `com.jore.epoc.SimulationBuilder`.
public class SimulationBuilder {
    private static let CHF = "CHF"
    private static var nextId: Int64 = 0

    private static func nextID() -> Int64 {
        let id = nextId
        nextId += 1
        return id
    }

    public static func builder() -> SimulationBuilder {
        SimulationBuilder()
    }

    // System parameters
    private var interestRate = Percent.of("5%")
    private var baseCurrency = Currency.getInstance("CHF")
    private var constructionCostsPerUnit = Money.of(CHF, 100)
    private var constructionCosts = Money.of(CHF, 1000000)
    private var rawMaterialUnitPrice = Money.of(CHF, 300)
    private var factoryConstructionCosts = Money.of(CHF, 1000000)
    private var demandCurveLowerPercent = Percent.of("80%")
    private var demandCurveLowerPrice = Money.of(CHF, 500)
    private var demandCurveHigherPercent = Percent.of("20%")
    private var demandCurveHigherPrice = Money.of(CHF, 1200)
    private var productLifecycleDuration = 100
    private var timeToBuildStorage = 0
    private var timeToBuildFactory = 0
    private var dailyCapacityPerProductionLine = 4
    private var factoryLaborCost = Money.of(CHF, 500000)
    private var marketNameValue = "Switzerland"
    private var marketDistributionCost = Money.of(CHF, 2000000)
    private var laborForceValue = 1000000
    private var buildingMaintenanceCostValue = Money.of(CHF, 10000)
    private var depreciationRateValue = Percent.of("15%")
    private var headquarterCostValue = Money.of(CHF, 1500000)
    private var inventoryManagementCostValue = Money.of(CHF, 500000)
    private var marketEntryCost = Money.of(CHF, 400000)
    private var productionCostValue = Money.of(CHF, 30)
    private var simulationPassiveSteps = 3
    private var pricePerPointQuality = Money.of(CHF, 200000)
    private var pricePerMarketingCampaign = Money.of(CHF, 500000)
    private var pricePerProductivityPoint = Money.of(CHF, 500000)
    private var factorDiscountRate = Percent.of("10%")
    // To be set in application
    private var initialOfferedPrice = Money.of(CHF, 800)
    private var initialIntendedSale = 1000
    private var productionLinesValue = 10
    private var productionLineConstructionCosts = Money.of(CHF, 100000)
    private var simulationStart = YearMonth.of(2020, 1)
    private var nameValue = "Unnamed Company"
    private var simulationNameValue = "Unnamed Simulation"
    private var numberOfSimulationStepsValue = 24
    private var orders: [any SimulationOrder] = []
    private var simulationValue: Simulation?

    @discardableResult
    public func name(_ name: String) -> SimulationBuilder {
        self.nameValue = name
        return self
    }

    @discardableResult
    public func simulationName(_ simulationName: String) -> SimulationBuilder {
        self.simulationNameValue = simulationName
        return self
    }

    @discardableResult
    public func numberOfSimulationSteps(_ numberOfSimulationSteps: Int) -> SimulationBuilder {
        self.numberOfSimulationStepsValue = numberOfSimulationSteps
        return self
    }

    @discardableResult
    public func passiveSteps(_ passiveSteps: Int) -> SimulationBuilder {
        self.simulationPassiveSteps = passiveSteps
        return self
    }

    @discardableResult
    public func laborForce(_ laborForce: Int) -> SimulationBuilder {
        self.laborForceValue = laborForce
        return self
    }

    @discardableResult
    public func simulation(_ simulation: Simulation) -> SimulationBuilder {
        self.simulationValue = simulation
        return self
    }

    // MARK: - Order Builders

    @discardableResult
    public func increaseCreditLine(_ executionMonth: YearMonth, _ increaseAmount: Money) -> SimulationBuilder {
        let order = AdjustCreditLineOrder()
        order.executionMonth = executionMonth
        order.amount = increaseAmount
        order.direction = .increase
        order.interestRate = interestRate
        orders.append(order)
        return self
    }

    @discardableResult
    public func buildFactory(_ executionMonth: YearMonth) -> SimulationBuilder {
        let order = BuildFactoryOrder()
        order.executionMonth = executionMonth
        order.constructionCost = factoryConstructionCosts
        order.constructionCostPerLine = productionLineConstructionCosts
        order.productionLines = productionLinesValue
        order.timeToBuild = timeToBuildFactory
        order.dailyCapacityPerProductionLine = dailyCapacityPerProductionLine
        order.productionLineLaborCost = factoryLaborCost
        orders.append(order)
        return self
    }

    @discardableResult
    public func buildStorage(_ executionMonth: YearMonth, _ storageCapacity: Int) -> SimulationBuilder {
        let order = BuildStorageOrder()
        order.executionMonth = executionMonth
        order.capacity = storageCapacity
        order.constructionCostPerUnit = constructionCostsPerUnit
        order.constructionCost = constructionCosts
        order.timeToBuild = timeToBuildStorage
        order.inventoryManagementCost = inventoryManagementCostValue
        orders.append(order)
        return self
    }

    @discardableResult
    public func buyRawMaterial(_ executionMonth: YearMonth, _ amount: Int) -> SimulationBuilder {
        let order = BuyRawMaterialOrder()
        order.executionMonth = executionMonth
        order.amount = amount
        order.unitPrice = rawMaterialUnitPrice
        orders.append(order)
        return self
    }

    @discardableResult
    public func enterMarket(_ executionMonth: YearMonth) -> SimulationBuilder {
        let order = EnterMarketOrder()
        order.executionMonth = executionMonth
        order.offeredPrice = initialOfferedPrice
        order.intentedProductSale = initialIntendedSale
        order.marketEntryCost = marketEntryCost
        orders.append(order)
        return self
    }

    @discardableResult
    public func changeAmountAndPriceOrder(_ executionMonth: YearMonth, _ amount: Int, _ price: Money) -> SimulationBuilder {
        let order = ChangeAmountAndPriceOrder()
        order.executionMonth = executionMonth
        order.intentedSales = amount
        order.offeredPrice = price
        orders.append(order)
        return self
    }

    @discardableResult
    public func increaseQuality(_ executionMonth: YearMonth, _ increaseQualityAmount: Money) -> SimulationBuilder {
        let order = IncreaseQualityOrder()
        order.executionMonth = executionMonth
        order.increaseQualityAmount = increaseQualityAmount
        orders.append(order)
        return self
    }

    @discardableResult
    public func increaseProductivity(_ executionMonth: YearMonth, _ increaseProductivityAmount: Money) -> SimulationBuilder {
        let order = IncreaseProductivityOrder()
        order.executionMonth = executionMonth
        order.increaseProductivityAmount = increaseProductivityAmount
        orders.append(order)
        return self
    }

    @discardableResult
    public func marketingCampaign(_ executionMonth: YearMonth, _ marketingCampaignAmount: Money) -> SimulationBuilder {
        let order = MarketingCampaignOrder()
        order.executionMonth = executionMonth
        order.marketingCampaignAmount = marketingCampaignAmount
        orders.append(order)
        return self
    }

    // MARK: - Build

    public func build() -> Company {
        let result = Company()
        result.id = SimulationBuilder.nextID()
        result.name = nameValue
        if simulationValue == nil {
            createSimulation()
            let market = createMarket()
            let marketSimulation = createMarketSimulation()
            marketSimulation.market = market
            simulationValue!.addMarketSimulation(marketSimulation)
        }
        simulationValue!.addCompany(result)
        let accounting = FinancialAccounting()
        accounting.id = SimulationBuilder.nextID()
        accounting.baseCurrency = baseCurrency
        result.accounting = accounting
        for order in orders {
            result.addSimulationOrder(order)
            if let enterMarketOrder = order as? EnterMarketOrder {
                enterMarketOrder.marketSimulation = simulationValue!.marketSimulations[0]
            }
            if let changeAmountAndPriceOrder = order as? ChangeAmountAndPriceOrder {
                changeAmountAndPriceOrder.market = simulationValue!.marketSimulations[0].market
            }
        }
        return result
    }

    // MARK: - Private

    private func createSimulation() {
        let settings = EpocSettings()
        simulationValue = Simulation()
        func addSetting(key: String, value: String) {
            let setting = EpocSetting()
            setting.settingKey = key
            setting.valueText = value
            settings.addSetting(setting)
        }
        addSetting(key: EpocSettings.PASSIVE_STEPS, value: "\(simulationPassiveSteps)")
        addSetting(key: EpocSettings.PRICE_PER_POINT_QUALITY, value: "\(pricePerPointQuality)")
        addSetting(key: EpocSettings.PRICE_PER_MARKETING_CAMPAIGN, value: "\(pricePerMarketingCampaign)")
        addSetting(key: EpocSettings.PRICE_PER_PRODUCTIVITY_POINT, value: "\(pricePerProductivityPoint)")
        addSetting(key: EpocSettings.FACTOR_DISCOUNT_RATE, value: "\(factorDiscountRate)")
        simulationValue!.settings = settings
        simulationValue!.id = SimulationBuilder.nextID()
        simulationValue!.name = simulationNameValue
        simulationValue!.startMonth = simulationStart
        simulationValue!.nrOfMonths = numberOfSimulationStepsValue
        simulationValue!.interestRate = interestRate
        simulationValue!.buildingMaintenanceCost = buildingMaintenanceCostValue
        simulationValue!.depreciationRate = depreciationRateValue
        simulationValue!.headquarterCost = headquarterCostValue
        simulationValue!.productionCost = productionCostValue
    }

    private func createMarket() -> Market {
        let market = Market()
        market.id = SimulationBuilder.nextID()
        market.name = marketNameValue
        market.laborForce = laborForceValue
        market.distributionCost = marketDistributionCost
        market.costToEnterMarket = marketEntryCost
        return market
    }

    private func createMarketSimulation() -> MarketSimulation {
        let marketSimulation = MarketSimulation()
        marketSimulation.id = SimulationBuilder.nextID()
        marketSimulation.startMonth = simulationStart
        marketSimulation.lowerPercent = demandCurveLowerPercent
        marketSimulation.lowerPrice = demandCurveLowerPrice
        marketSimulation.higherPercent = demandCurveHigherPercent
        marketSimulation.higherPrice = demandCurveHigherPrice
        marketSimulation.productLifecycleDuration = productLifecycleDuration
        return marketSimulation
    }
}
