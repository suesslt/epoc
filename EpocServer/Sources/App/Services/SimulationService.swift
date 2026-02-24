import Vapor
import Fluent

/// Service layer for simulation operations.
/// Equivalent to `com.jore.epoc.services.impl.SimulationServiceImpl`.
struct SimulationService {
    let db: Database

    // MARK: - Order Operations

    func buildFactory(_ dto: BuildFactoryDTO) async throws {
        let company = try await CompanyModel.find(dto.companyId, on: db)!
        let simulation = try await company.$simulation.get(on: db)
        let settings = try await simulation.$settings.get(on: db)!
        let settingEntries = try await settings.$settingEntries.get(on: db)
        let epocSettings = mapToEpocSettings(settings, entries: settingEntries)
        try logicalValidation(simulationStartMonth: simulation.startMonth, nrOfMonths: simulation.nrOfMonths, executionMonth: dto.executionMonth)
        let order = SimulationOrderModel()
        order.$company.id = dto.companyId
        order.orderType = "BuildFactory"
        order.executionMonth = dto.executionMonth
        order.isExecuted = false
        order.intParam1 = dto.productionLines
        order.intParam2 = epocSettings.getTimeToBuild()
        order.amountValue = epocSettings.getFactoryConstructionCost().amount.doubleValue
        order.amountCurrency = epocSettings.getFactoryConstructionCost().currency.currencyCode
        order.moneyParam1Amount = epocSettings.getFactoryConstructionCostsPerLine().amount.doubleValue
        order.moneyParam1Currency = epocSettings.getFactoryConstructionCostsPerLine().currency.currencyCode
        order.doubleParam1 = Double(epocSettings.getDailyCapacityPerProductionLine())
        order.moneyParam2Amount = epocSettings.getProductionLineLaborCost().amount.doubleValue
        order.moneyParam2Currency = epocSettings.getProductionLineLaborCost().currency.currencyCode
        try await order.save(on: db)
    }

    func buildStorage(_ dto: BuildStorageDTO) async throws {
        let company = try await CompanyModel.find(dto.companyId, on: db)!
        let simulation = try await company.$simulation.get(on: db)
        let settings = try await simulation.$settings.get(on: db)!
        let settingEntries = try await settings.$settingEntries.get(on: db)
        let epocSettings = mapToEpocSettings(settings, entries: settingEntries)
        try logicalValidation(simulationStartMonth: simulation.startMonth, nrOfMonths: simulation.nrOfMonths, executionMonth: dto.executionMonth)
        let order = SimulationOrderModel()
        order.$company.id = dto.companyId
        order.orderType = "BuildStorage"
        order.executionMonth = dto.executionMonth
        order.isExecuted = false
        order.intParam1 = dto.capacity
        order.intParam2 = epocSettings.getStorageConstructionMonths()
        order.amountValue = epocSettings.getStorageFixedCost().amount.doubleValue
        order.amountCurrency = epocSettings.getStorageFixedCost().currency.currencyCode
        order.moneyParam1Amount = epocSettings.getStorageCostPerUnit().amount.doubleValue
        order.moneyParam1Currency = epocSettings.getStorageCostPerUnit().currency.currencyCode
        order.moneyParam2Amount = epocSettings.getInventoryManagementCost().amount.doubleValue
        order.moneyParam2Currency = epocSettings.getInventoryManagementCost().currency.currencyCode
        try await order.save(on: db)
    }

    func buyRawMaterial(_ dto: BuyRawMaterialDTO) async throws {
        let company = try await CompanyModel.find(dto.companyId, on: db)!
        let simulation = try await company.$simulation.get(on: db)
        let settings = try await simulation.$settings.get(on: db)!
        let settingEntries = try await settings.$settingEntries.get(on: db)
        let epocSettings = mapToEpocSettings(settings, entries: settingEntries)
        try logicalValidation(simulationStartMonth: simulation.startMonth, nrOfMonths: simulation.nrOfMonths, executionMonth: dto.executionMonth)
        let order = SimulationOrderModel()
        order.$company.id = dto.companyId
        order.orderType = "BuyRawMaterial"
        order.executionMonth = dto.executionMonth
        order.isExecuted = false
        order.intParam1 = dto.amount
        order.amountValue = epocSettings.getRawMaterialUnitPrice().amount.doubleValue
        order.amountCurrency = epocSettings.getRawMaterialUnitPrice().currency.currencyCode
        try await order.save(on: db)
    }

    func increaseCreditLine(_ dto: AdjustCreditLineDTO) async throws {
        let company = try await CompanyModel.find(dto.companyId, on: db)!
        let simulation = try await company.$simulation.get(on: db)
        let settings = try await simulation.$settings.get(on: db)!
        let settingEntries = try await settings.$settingEntries.get(on: db)
        let epocSettings = mapToEpocSettings(settings, entries: settingEntries)
        try logicalValidation(simulationStartMonth: simulation.startMonth, nrOfMonths: simulation.nrOfMonths, executionMonth: dto.executionMonth)
        let amount = Money.parse(dto.amount)
        let order = SimulationOrderModel()
        order.$company.id = dto.companyId
        order.orderType = "AdjustCreditLine"
        order.executionMonth = dto.executionMonth
        order.isExecuted = false
        order.amountValue = amount.amount.doubleValue
        order.amountCurrency = amount.currency.currencyCode
        order.stringParam1 = "INCREASE"
        order.doubleParam1 = epocSettings.getDebtInterestRate().doubleValue
        try await order.save(on: db)
    }

    func decreaseCreditLine(_ dto: AdjustCreditLineDTO) async throws {
        let company = try await CompanyModel.find(dto.companyId, on: db)!
        let simulation = try await company.$simulation.get(on: db)
        let settings = try await simulation.$settings.get(on: db)!
        let settingEntries = try await settings.$settingEntries.get(on: db)
        let epocSettings = mapToEpocSettings(settings, entries: settingEntries)
        try logicalValidation(simulationStartMonth: simulation.startMonth, nrOfMonths: simulation.nrOfMonths, executionMonth: dto.executionMonth)
        let amount = Money.parse(dto.amount)
        let order = SimulationOrderModel()
        order.$company.id = dto.companyId
        order.orderType = "AdjustCreditLine"
        order.executionMonth = dto.executionMonth
        order.isExecuted = false
        order.amountValue = amount.amount.doubleValue
        order.amountCurrency = amount.currency.currencyCode
        order.stringParam1 = "DECREASE"
        order.doubleParam1 = epocSettings.getDebtInterestRate().doubleValue
        try await order.save(on: db)
    }

    func enterMarket(_ dto: EnterMarketDTO) async throws {
        let company = try await CompanyModel.find(dto.companyId, on: db)!
        let simulation = try await company.$simulation.get(on: db)
        let market = try await MarketModel.find(dto.marketId, on: db)!
        try logicalValidation(simulationStartMonth: simulation.startMonth, nrOfMonths: simulation.nrOfMonths, executionMonth: dto.executionMonth)
        // Find or create MarketSimulation
        var marketSim = try await MarketSimulationModel.query(on: db)
            .filter(\.$market.$id == dto.marketId)
            .filter(\.$simulation.$id == simulation.id!)
            .first()
        if marketSim == nil {
            let settings = try await simulation.$settings.get(on: db)!
            let settingEntries = try await settings.$settingEntries.get(on: db)
            let epocSettings = mapToEpocSettings(settings, entries: settingEntries)
            let newMarketSim = MarketSimulationModel()
            newMarketSim.$market.id = dto.marketId
            newMarketSim.$simulation.id = simulation.id!
            newMarketSim.startMonth = dto.executionMonth
            newMarketSim.higherPercent = epocSettings.getDemandHigherPercent().doubleValue
            newMarketSim.higherPriceAmount = epocSettings.getDemandHigherPrice().amount.doubleValue
            newMarketSim.higherPriceCurrency = epocSettings.getDemandHigherPrice().currency.currencyCode
            newMarketSim.lowerPercent = epocSettings.getDemandLowerPercent().doubleValue
            newMarketSim.lowerPriceAmount = epocSettings.getDemandLowerPrice().amount.doubleValue
            newMarketSim.lowerPriceCurrency = epocSettings.getDemandLowerPrice().currency.currencyCode
            newMarketSim.productLifecycleDuration = epocSettings.getProductLifecycleDuration()
            try await newMarketSim.save(on: db)
            marketSim = newMarketSim
        }
        let order = SimulationOrderModel()
        order.$company.id = dto.companyId
        order.orderType = "EnterMarket"
        order.executionMonth = dto.executionMonth
        order.isExecuted = false
        order.intParam1 = dto.intentedProductSales
        let offeredPrice = Money.parse(dto.offeredPrice)
        order.amountValue = offeredPrice.amount.doubleValue
        order.amountCurrency = offeredPrice.currency.currencyCode
        order.moneyParam1Amount = market.costToEnterMarketAmount
        order.moneyParam1Currency = market.costToEnterMarketCurrency
        order.marketSimulationId = marketSim!.id
        try await order.save(on: db)
    }

    func increaseQuality(_ dto: IncreaseQualityDTO) async throws {
        try logicalValidationForCompany(dto.companyId, executionMonth: dto.executionMonth)
        let amount = Money.parse(dto.increaseQualityAmount)
        let order = SimulationOrderModel()
        order.$company.id = dto.companyId
        order.orderType = "IncreaseQuality"
        order.executionMonth = dto.executionMonth
        order.isExecuted = false
        order.amountValue = amount.amount.doubleValue
        order.amountCurrency = amount.currency.currencyCode
        try await order.save(on: db)
    }

    func increaseProductivity(_ dto: IncreaseProductivityDTO) async throws {
        try logicalValidationForCompany(dto.companyId, executionMonth: dto.executionMonth)
        let amount = Money.parse(dto.increaseProductivityAmount)
        let order = SimulationOrderModel()
        order.$company.id = dto.companyId
        order.orderType = "IncreaseProductivity"
        order.executionMonth = dto.executionMonth
        order.isExecuted = false
        order.amountValue = amount.amount.doubleValue
        order.amountCurrency = amount.currency.currencyCode
        try await order.save(on: db)
    }

    func runMarketingCampaign(_ dto: RunMarketingCampaignDTO) async throws {
        try logicalValidationForCompany(dto.companyId, executionMonth: dto.executionMonth)
        let amount = Money.parse(dto.campaignAmount)
        let order = SimulationOrderModel()
        order.$company.id = dto.companyId
        order.orderType = "MarketingCampaign"
        order.executionMonth = dto.executionMonth
        order.isExecuted = false
        order.amountValue = amount.amount.doubleValue
        order.amountCurrency = amount.currency.currencyCode
        try await order.save(on: db)
    }

    func setIntendedSalesAndPrice(_ dto: IntendedSalesAndPriceDTO) async throws {
        try logicalValidationForCompany(dto.companyId, executionMonth: dto.executionMonth)
        let price = Money.parse(dto.price)
        let order = SimulationOrderModel()
        order.$company.id = dto.companyId
        order.orderType = "ChangeAmountAndPrice"
        order.executionMonth = dto.executionMonth
        order.isExecuted = false
        order.intParam1 = dto.intentedSales
        order.amountValue = price.amount.doubleValue
        order.amountCurrency = price.currency.currencyCode
        order.marketId = dto.marketId
        try await order.save(on: db)
    }

    // MARK: - Finish Move (triggers simulation)

    func finishMoveFor(companySimulationStepId: Int64) async throws {
        let engine = SimulationEngineService(db: db)
        try await engine.finishMove(companySimulationStepId: companySimulationStepId)
    }

    // MARK: - Query Operations

    func getSimulationsForOwner(ownerId: Int64) async throws -> [SimulationDTO] {
        let simulations = try await SimulationModel.query(on: db).all()
        return simulations.map { sim in
            SimulationDTO(
                id: sim.id,
                name: sim.name,
                startMonth: sim.startMonth,
                nrOfMonths: sim.nrOfMonths
            )
        }
    }

    func getSimulationStatistics(simulationId: Int64) async throws -> SimulationStatisticsDTO {
        let simulation = try await SimulationModel.find(simulationId, on: db)!
        let distributionSteps = try await DistributionStepModel.query(on: db)
            .join(DistributionInMarketModel.self, on: \DistributionStepModel.$distributionInMarket.$id == \DistributionInMarketModel.$id)
            .join(CompanyModel.self, on: \DistributionInMarketModel.$company.$id == \CompanyModel.$id)
            .filter(CompanyModel.self, \.$simulation.$id == simulation.id!)
            .all()
        let totalSold = distributionSteps.reduce(0) { $0 + $1.soldProducts }
        return SimulationStatisticsDTO(totalSoldProducts: totalSold)
    }

    func getCurrentCompanySimulationStep(companyId: Int64) async throws -> CompanySimulationStepDTO? {
        let company = try await CompanyModel.find(companyId, on: db)!
        let factories = try await company.$factories.get(on: db)
        let storages = try await company.$storages.get(on: db)
        let distributions = try await company.$distributionInMarkets.get(on: db)
        let messages = try await company.$messages.get(on: db)
        let markets = try await MarketModel.query(on: db).all()
        // Find the latest open step
        let companyStep = try await CompanySimulationStepModel.query(on: db)
            .filter(\.$company.$id == companyId)
            .filter(\.$isOpen == true)
            .sort(\.$id, .descending)
            .first()
        guard let step = companyStep else { return nil }
        let simStep = try await step.$simulationStep.get(on: db)
        var dto = CompanySimulationStepDTO()
        dto.id = step.id
        dto.companyName = company.name
        dto.simulationMonth = simStep.simulationMonth
        dto.factories = factories.map { FactoryDTO(id: $0.id) }
        dto.storages = storages.map { StorageDTO(id: $0.id) }
        dto.distributionInMarkets = distributions.map { DistributionInMarketDTO(id: $0.id) }
        dto.markets = markets.map { MarketDTO(id: $0.id, name: $0.name, laborForce: $0.laborForce) }
        dto.messages = messages.map { MessageDTO(level: $0.level, message: $0.message, relevantMonth: $0.relevantMonth) }
        return dto
    }

    // MARK: - Helpers

    private func logicalValidation(simulationStartMonth: String, nrOfMonths: Int, executionMonth: String) throws {
        let start = YearMonth.parse(simulationStartMonth)
        let exec = YearMonth.parse(executionMonth)
        let end = start.plusMonths(nrOfMonths)
        guard !exec.isBefore(start) && !exec.isAfter(end) else {
            throw Abort(.badRequest, reason: "Execution month is outside simulation range")
        }
    }

    private func logicalValidationForCompany(_ companyId: Int64, executionMonth: String) async throws {
        let company = try await CompanyModel.find(companyId, on: db)!
        let simulation = try await company.$simulation.get(on: db)
        try logicalValidation(simulationStartMonth: simulation.startMonth, nrOfMonths: simulation.nrOfMonths, executionMonth: executionMonth)
    }

    private func mapToEpocSettings(_ model: EpocSettingsModel, entries: [EpocSettingModel]) -> EpocSettings {
        let settings = EpocSettings()
        settings.isTemplate = model.isTemplate
        for entry in entries {
            let setting = EpocSetting()
            setting.settingKey = entry.settingKey
            setting.valueText = entry.valueText
            setting.settingFormat = entry.settingFormat ?? ""
            settings.addSetting(setting)
        }
        return settings
    }
}

// MARK: - YearMonth.isAfter extension
extension YearMonth {
    func isAfter(_ other: YearMonth) -> Bool {
        !self.isBefore(other) && self != other
    }
}
