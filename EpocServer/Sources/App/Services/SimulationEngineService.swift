import Vapor
import Fluent

/// Service that bridges the Fluent persistence layer and the in-memory domain model.
/// Loads the complete simulation graph from DB, executes the simulation logic,
/// and persists all changes back.
///
/// Equivalent to the simulation execution path in Java's `SimulationServiceImpl.finishMove()`.
struct SimulationEngineService {
    let db: Database

    // MARK: - Public Entry Point

    /// Finishes a company's move and triggers simulation if all companies are done.
    /// This is the main entry point called from SimulationService.finishMoveFor().
    func finishMove(companySimulationStepId: Int64) async throws {
        // 1. Load complete simulation graph into memory
        let companyStepModel = try await CompanySimulationStepModel.find(companySimulationStepId, on: db)!
        let simStepModel = try await companyStepModel.$simulationStep.get(on: db)
        let simulationModel = try await simStepModel.$simulation.get(on: db)
        let simulation = try await loadSimulation(simulationModel.id!)

        // 2. Find the corresponding domain CompanySimulationStep
        let companySimulationStep = findCompanySimulationStep(companySimulationStepId, in: simulation)

        // 3. Execute in-memory: mark step as done, trigger simulation if all companies finished
        simulation.finishCompanyStep(companySimulationStep)

        // 4. Persist all changes back to DB
        try await persistSimulation(simulation)
    }

    // MARK: - Load Simulation Graph

    /// Loads the complete simulation from DB into an in-memory domain model graph.
    func loadSimulation(_ simulationId: Int64) async throws -> Simulation {
        let simModel = try await SimulationModel.find(simulationId, on: db)!

        // Eager-load all related models
        let companyModels = try await simModel.$companies.get(on: db)
        let simStepModels = try await simModel.$simulationSteps.get(on: db)
        let marketSimModels = try await simModel.$marketSimulations.get(on: db)

        // Load settings
        var settings: EpocSettings? = nil
        if let settingsModel = try await simModel.$settings.get(on: db) {
            let entries = try await settingsModel.$settingEntries.get(on: db)
            settings = mapSettings(settingsModel, entries: entries)
        }

        // Build Simulation domain object
        let simulation = Simulation()
        simulation.id = simModel.id
        simulation.name = simModel.name
        simulation.startMonth = YearMonth.parse(simModel.startMonth)
        simulation.nrOfMonths = simModel.nrOfMonths
        simulation.isStarted = simModel.isStarted
        simulation.isFinished = simModel.isFinished
        simulation.interestRate = Percent.of(simModel.interestRate)
        simulation.settings = settings
        simulation.buildingMaintenanceCost = Money.of(
            Currency.getInstance(simModel.buildingMaintenanceCurrency),
            Decimal(simModel.buildingMaintenanceAmount)
        )
        simulation.depreciationRate = Percent.of(simModel.depreciationRate)
        simulation.headquarterCost = Money.of(
            Currency.getInstance(simModel.headquarterCurrency),
            Decimal(simModel.headquarterAmount)
        )
        simulation.productionCost = Money.of(
            Currency.getInstance(simModel.productionCostCurrency),
            Decimal(simModel.productionCostAmount)
        )

        // Load markets (needed by MarketSimulations and orders)
        var marketCache: [Int64: Market] = [:]
        for msModel in marketSimModels {
            let marketModel = try await msModel.$market.get(on: db)
            if marketCache[marketModel.id!] == nil {
                marketCache[marketModel.id!] = mapMarket(marketModel)
            }
        }

        // Build MarketSimulations
        var marketSimulationCache: [Int64: MarketSimulation] = [:]
        for msModel in marketSimModels {
            let ms = MarketSimulation()
            ms.id = msModel.id
            ms.startMonth = YearMonth.parse(msModel.startMonth)
            ms.higherPrice = Money.of(Currency.getInstance(msModel.higherPriceCurrency), Decimal(msModel.higherPriceAmount))
            ms.higherPercent = Percent.of(msModel.higherPercent)
            ms.lowerPrice = Money.of(Currency.getInstance(msModel.lowerPriceCurrency), Decimal(msModel.lowerPriceAmount))
            ms.lowerPercent = Percent.of(msModel.lowerPercent)
            ms.productLifecycleDuration = msModel.productLifecycleDuration
            ms.market = marketCache[msModel.$market.id]!
            simulation.addMarketSimulation(ms)
            marketSimulationCache[msModel.id!] = ms
        }

        // Build Companies
        var companyCache: [Int64: Company] = [:]
        for companyModel in companyModels {
            let company = Company()
            company.id = companyModel.id
            company.name = companyModel.name
            company.qualityFactor = companyModel.qualityFactor
            company.marketingFactor = companyModel.marketingFactor
            company.productivityFactor = companyModel.productivityFactor

            // Accounting
            company.accounting = try await loadAccounting(companyModel.id!)
            simulation.addCompany(company)
            companyCache[companyModel.id!] = company

            // Factories
            let factoryModels = try await companyModel.$factories.get(on: db)
            for fm in factoryModels {
                let factory = Factory()
                factory.id = fm.id
                factory.productionLines = fm.productionLines
                factory.productionStartMonth = YearMonth.parse(fm.productionStartMonth)
                factory.dailyCapacityPerProductionLine = fm.dailyCapacityPerProductionLine
                factory.productionLineLaborCost = Money.of(Currency.getInstance(fm.labourCostCurrency), Decimal(fm.labourCostAmount))
                company.addFactory(factory)
            }

            // Storages
            let storageModels = try await companyModel.$storages.get(on: db)
            for sm in storageModels {
                let storage = Storage()
                storage.id = sm.id
                storage.capacity = sm.capacity
                storage.storageStartMonth = YearMonth.parse(sm.storageStartMonth)
                storage.storedProducts = sm.storedProducts
                storage.storedRawMaterials = sm.storedRawMaterials
                storage.inventoryManagementCost = Money.of(Currency.getInstance(sm.inventoryManagementCostCurrency), Decimal(sm.inventoryManagementCostAmount))
                company.addStorage(storage)
            }

            // DistributionInMarkets
            let distModels = try await companyModel.$distributionInMarkets.get(on: db)
            for dm in distModels {
                let dist = DistributionInMarket()
                dist.id = dm.id
                if let priceAmount = dm.offeredPriceAmount, let priceCurrency = dm.offeredPriceCurrency {
                    dist.offeredPrice = Money.of(Currency.getInstance(priceCurrency), Decimal(priceAmount))
                }
                dist.intentedProductSale = dm.intentedProductSale
                let marketSim = marketSimulationCache[dm.$marketSimulation.id]!
                marketSim.addDistributionInMarket(dist)
                company.addDistributionInMarket(dist)

                // Load existing distribution steps
                let distStepModels = try await dm.$distributionSteps.get(on: db)
                for dsm in distStepModels {
                    let ds = DistributionStep()
                    ds.id = dsm.id
                    ds.soldProducts = dsm.soldProducts
                    ds.intentedProductSale = dsm.intentedProductSale
                    ds.marketPotentialForProduct = dsm.marketPotentialForProduct
                    if let pa = dsm.offeredPriceAmount, let pc = dsm.offeredPriceCurrency {
                        ds.offeredPrice = Money.of(Currency.getInstance(pc), Decimal(pa))
                    }
                    dist.addDistributionStep(ds)
                    // Link to CompanySimulationStep is set below
                }
            }

            // Orders
            let orderModels = try await companyModel.$simulationOrders.get(on: db)
            for om in orderModels {
                let order = reconstructOrder(om, marketSimulationCache: marketSimulationCache, marketCache: marketCache)
                company.addSimulationOrder(order)
            }

            // Messages (load existing)
            let messageModels = try await companyModel.$messages.get(on: db)
            for mm in messageModels {
                let msg = Message()
                msg.id = mm.id
                msg.relevantMonth = YearMonth.parse(mm.relevantMonth)
                msg.message = mm.message
                msg.level = MessageLevel(rawValue: mm.level) ?? .information
                company.addMessage(msg)
            }
        }

        // Build SimulationSteps with CompanySimulationSteps
        for stepModel in simStepModels {
            let step = SimulationStep()
            step.id = stepModel.id
            step.simulationMonth = YearMonth.parse(stepModel.simulationMonth)
            step.isOpen = stepModel.isOpen
            simulation.addSimulationStep(step)

            let companyStepModels = try await stepModel.$companySimulationSteps.get(on: db)
            for csm in companyStepModels {
                let cs = CompanySimulationStep()
                cs.id = csm.id
                cs.isOpen = csm.isOpen
                let company = companyCache[csm.$company.id]!
                company.addCompanySimulationStep(cs)
                step.addCompanySimulationStep(cs)

                // Link distribution steps to their company simulation steps
                let distStepModels = try await csm.$distributionSteps.get(on: db)
                for dsm in distStepModels {
                    // Find the matching DistributionStep in the already-loaded distributions
                    for dist in company.distributionInMarkets {
                        if let ds = dist.distributionSteps.first(where: { $0.id == dsm.id }) {
                            cs.addDistributionStep(ds)
                        }
                    }
                }
            }
        }

        return simulation
    }

    // MARK: - Persist Simulation

    /// Persists all changes from the in-memory simulation back to the database.
    func persistSimulation(_ simulation: Simulation) async throws {
        // Update simulation flags
        let simModel = try await SimulationModel.find(simulation.id!, on: db)!
        simModel.isStarted = simulation.isStarted
        simModel.isFinished = simulation.isFinished
        try await simModel.save(on: db)

        // Persist companies
        for company in simulation.companies {
            try await persistCompany(company)
        }

        // Persist simulation steps
        for step in simulation.simulationSteps {
            try await persistSimulationStep(step)
        }

        // Persist market simulation distribution data
        for marketSim in simulation.marketSimulations {
            for dist in marketSim.distributionInMarkets {
                try await persistDistributionInMarket(dist)
            }
        }
    }

    // MARK: - Persist Company

    private func persistCompany(_ company: Company) async throws {
        let companyModel = try await CompanyModel.find(company.id!, on: db)!
        companyModel.qualityFactor = company.qualityFactor
        companyModel.marketingFactor = company.marketingFactor
        companyModel.productivityFactor = company.productivityFactor
        try await companyModel.save(on: db)

        // Persist factories (new ones created by BuildFactoryOrder)
        for factory in company.factories {
            if factory.id == nil {
                let fm = FactoryModel()
                fm.$company.id = company.id!
                fm.productionLines = factory.productionLines
                fm.productionStartMonth = factory.productionStartMonth.description
                fm.dailyCapacityPerProductionLine = factory.dailyCapacityPerProductionLine
                fm.labourCostAmount = factory.productionLineLaborCost.amount.doubleValue
                fm.labourCostCurrency = factory.productionLineLaborCost.currency.currencyCode
                try await fm.save(on: db)
                factory.id = fm.id
            }
        }

        // Persist storages (new ones created by BuildStorageOrder, and updated inventory)
        for storage in company.storages {
            if storage.id == nil {
                let sm = StorageModel()
                sm.$company.id = company.id!
                sm.capacity = storage.capacity
                sm.storageStartMonth = storage.storageStartMonth.description
                sm.storedProducts = storage.storedProducts
                sm.storedRawMaterials = storage.storedRawMaterials
                sm.inventoryManagementCostAmount = storage.inventoryManagementCost.amount.doubleValue
                sm.inventoryManagementCostCurrency = storage.inventoryManagementCost.currency.currencyCode
                try await sm.save(on: db)
                storage.id = sm.id
            } else {
                let sm = try await StorageModel.find(storage.id!, on: db)!
                sm.storedProducts = storage.storedProducts
                sm.storedRawMaterials = storage.storedRawMaterials
                try await sm.save(on: db)
            }
        }

        // Persist orders (mark executed)
        for order in company.simulationOrders {
            if let orderId = order.id {
                let om = try await SimulationOrderModel.find(orderId, on: db)!
                om.isExecuted = order.isExecuted
                try await om.save(on: db)
            }
        }

        // Persist new messages
        for message in company.messages {
            if message.id == nil {
                let mm = MessageModel()
                mm.$company.id = company.id!
                mm.relevantMonth = message.relevantMonth.description
                mm.message = message.message
                mm.level = message.level.rawValue
                try await mm.save(on: db)
                message.id = mm.id
            }
        }

        // Persist accounting
        try await persistAccounting(company.accounting, companyId: company.id!)
    }

    // MARK: - Persist SimulationStep

    private func persistSimulationStep(_ step: SimulationStep) async throws {
        if step.id == nil {
            // New step created during simulation (by getActiveSimulationStep / passive steps)
            let sm = SimulationStepModel()
            sm.$simulation.id = step.simulation.id!
            sm.simulationMonth = step.simulationMonth.description
            sm.isOpen = step.isOpen
            try await sm.save(on: db)
            step.id = sm.id

            for cs in step.companySimulationSteps {
                let csm = CompanySimulationStepModel()
                csm.$simulationStep.id = sm.id!
                csm.$company.id = cs.company.id!
                csm.isOpen = cs.isOpen
                try await csm.save(on: db)
                cs.id = csm.id

                // Persist distribution steps
                for ds in cs.distributionSteps {
                    try await persistDistributionStep(ds, companySimulationStepId: csm.id!)
                }
            }
        } else {
            let sm = try await SimulationStepModel.find(step.id!, on: db)!
            sm.isOpen = step.isOpen
            try await sm.save(on: db)

            for cs in step.companySimulationSteps {
                if cs.id == nil {
                    let csm = CompanySimulationStepModel()
                    csm.$simulationStep.id = step.id!
                    csm.$company.id = cs.company.id!
                    csm.isOpen = cs.isOpen
                    try await csm.save(on: db)
                    cs.id = csm.id
                } else {
                    let csm = try await CompanySimulationStepModel.find(cs.id!, on: db)!
                    csm.isOpen = cs.isOpen
                    try await csm.save(on: db)
                }

                // Persist distribution steps
                for ds in cs.distributionSteps {
                    try await persistDistributionStep(ds, companySimulationStepId: cs.id!)
                }
            }
        }
    }

    // MARK: - Persist DistributionStep

    private func persistDistributionStep(_ ds: DistributionStep, companySimulationStepId: Int64) async throws {
        if ds.id == nil {
            let dsm = DistributionStepModel()
            dsm.$distributionInMarket.id = ds.distributionInMarket.id!
            dsm.$companySimulationStep.id = companySimulationStepId
            dsm.soldProducts = ds.soldProducts
            dsm.intentedProductSale = ds.intentedProductSale ?? 0
            dsm.marketPotentialForProduct = ds.marketPotentialForProduct
            if let price = ds.offeredPrice {
                dsm.offeredPriceAmount = price.amount.doubleValue
                dsm.offeredPriceCurrency = price.currency.currencyCode
            }
            try await dsm.save(on: db)
            ds.id = dsm.id
        } else {
            let dsm = try await DistributionStepModel.find(ds.id!, on: db)!
            dsm.soldProducts = ds.soldProducts
            dsm.intentedProductSale = ds.intentedProductSale ?? 0
            dsm.marketPotentialForProduct = ds.marketPotentialForProduct
            if let price = ds.offeredPrice {
                dsm.offeredPriceAmount = price.amount.doubleValue
                dsm.offeredPriceCurrency = price.currency.currencyCode
            }
            try await dsm.save(on: db)
        }
    }

    // MARK: - Persist DistributionInMarket

    private func persistDistributionInMarket(_ dist: DistributionInMarket) async throws {
        if dist.id == nil {
            // New distribution created by EnterMarketOrder
            let dm = DistributionInMarketModel()
            dm.$company.id = dist.company.id!
            dm.$marketSimulation.id = dist.marketSimulation.id!
            if let price = dist.offeredPrice {
                dm.offeredPriceAmount = price.amount.doubleValue
                dm.offeredPriceCurrency = price.currency.currencyCode
            }
            dm.intentedProductSale = dist.intentedProductSale
            try await dm.save(on: db)
            dist.id = dm.id
        } else {
            let dm = try await DistributionInMarketModel.find(dist.id!, on: db)!
            if let price = dist.offeredPrice {
                dm.offeredPriceAmount = price.amount.doubleValue
                dm.offeredPriceCurrency = price.currency.currencyCode
            }
            dm.intentedProductSale = dist.intentedProductSale
            try await dm.save(on: db)
        }
    }

    // MARK: - Accounting Load/Persist

    private func loadAccounting(_ companyId: Int64) async throws -> FinancialAccounting {
        let companyModel = try await CompanyModel.find(companyId, on: db)!
        let accounting = FinancialAccounting()

        guard let am = try await companyModel.$accounting.get(on: db) else {
            // No saved accounting yet — return fresh one with default accounts
            return accounting
        }

        accounting.id = am.id
        accounting.baseCurrency = Currency.getInstance(am.baseCurrency)

        // Load accounts and set start balances
        let accountModels = try await am.$accounts.get(on: db)
        for accountModel in accountModels {
            if let account = accounting.accounts.first(where: { $0.number == accountModel.number }) {
                account.id = accountModel.id
                account.startBalance = Decimal(accountModel.startBalance)
            }
        }

        // Load journal entries with bookings
        let journalModels = try await am.$journalEntries.get(on: db)
        for jm in journalModels {
            let bookingModels = try await jm.$bookings.get(on: db)
            let journalEntry = JournalEntry()
            journalEntry.id = jm.id
            journalEntry.bookingText = jm.bookingText
            journalEntry.bookingDate = jm.bookingDate
            journalEntry.valueDate = jm.valueDate
            for bm in bookingModels {
                let booking = Booking()
                booking.id = bm.id
                booking.amount = Decimal(bm.amount)
                // Link to accounts
                let debitAccount = accounting.accounts.first(where: { $0.id == bm.$debitAccount.id })!
                let creditAccount = accounting.accounts.first(where: { $0.id == bm.$creditAccount.id })!
                debitAccount.debit(booking)
                creditAccount.credit(booking)
                journalEntry.addBooking(booking)
            }
            accounting.journalEntries.append(journalEntry)
        }

        return accounting
    }

    private func persistAccounting(_ accounting: FinancialAccounting, companyId: Int64) async throws {
        let companyModel = try await CompanyModel.find(companyId, on: db)!

        if accounting.id == nil {
            // Create new accounting in DB
            let am = FinancialAccountingModel()
            am.baseCurrency = accounting.baseCurrency.currencyCode
            try await am.save(on: db)
            accounting.id = am.id

            // Link company to accounting
            companyModel.$accounting.id = am.id
            try await companyModel.save(on: db)

            // Create account records
            for account in accounting.accounts {
                let accountModel = AccountModel()
                accountModel.$accounting.id = am.id!
                accountModel.accountType = account.accountType == .balanceSheet ? "BALANCE_SHEET" : "INCOME_STATEMENT"
                accountModel.number = account.number
                accountModel.name = account.name
                accountModel.startBalance = account.startBalance.doubleValue
                try await accountModel.save(on: db)
                account.id = accountModel.id
            }
        }

        let accountingId = accounting.id!

        // Persist new journal entries only (entries without an id)
        for journalEntry in accounting.journalEntries where journalEntry.id == nil {
            let jm = JournalEntryModel()
            jm.$accounting.id = accountingId
            jm.bookingText = journalEntry.bookingText
            jm.bookingDate = journalEntry.bookingDate
            jm.valueDate = journalEntry.valueDate
            try await jm.save(on: db)
            journalEntry.id = jm.id

            for booking in journalEntry.bookings {
                let bm = BookingModel()
                bm.$journalEntry.id = jm.id!
                bm.$debitAccount.id = booking.debitAccount!.id!
                bm.$creditAccount.id = booking.creditAccount!.id!
                bm.amount = booking.amount.doubleValue
                try await bm.save(on: db)
                booking.id = bm.id
            }
        }
    }

    // MARK: - Order Reconstruction

    /// Reconstructs a typed SimulationOrder domain object from a generic SimulationOrderModel.
    private func reconstructOrder(_ model: SimulationOrderModel, marketSimulationCache: [Int64: MarketSimulation], marketCache: [Int64: Market]) -> any SimulationOrder {
        switch model.orderType {
        case "AdjustCreditLine":
            let order = AdjustCreditLineOrder()
            order.id = model.id
            order.executionMonth = YearMonth.parse(model.executionMonth)
            order.isExecuted = model.isExecuted
            order.amount = Money.of(Currency.getInstance(model.amountCurrency!), Decimal(model.amountValue!))
            order.direction = CreditEventDirection(rawValue: model.stringParam1!) ?? .increase
            order.interestRate = Percent.of(model.doubleParam1!)
            return order

        case "BuildFactory":
            let order = BuildFactoryOrder()
            order.id = model.id
            order.executionMonth = YearMonth.parse(model.executionMonth)
            order.isExecuted = model.isExecuted
            order.productionLines = model.intParam1!
            order.timeToBuild = model.intParam2!
            order.constructionCost = Money.of(Currency.getInstance(model.amountCurrency!), Decimal(model.amountValue!))
            order.constructionCostPerLine = Money.of(Currency.getInstance(model.moneyParam1Currency!), Decimal(model.moneyParam1Amount!))
            order.dailyCapacityPerProductionLine = Int(model.doubleParam1!)
            order.productionLineLaborCost = Money.of(Currency.getInstance(model.moneyParam2Currency!), Decimal(model.moneyParam2Amount!))
            return order

        case "BuildStorage":
            let order = BuildStorageOrder()
            order.id = model.id
            order.executionMonth = YearMonth.parse(model.executionMonth)
            order.isExecuted = model.isExecuted
            order.capacity = model.intParam1!
            order.timeToBuild = model.intParam2!
            order.constructionCost = Money.of(Currency.getInstance(model.amountCurrency!), Decimal(model.amountValue!))
            order.constructionCostPerUnit = Money.of(Currency.getInstance(model.moneyParam1Currency!), Decimal(model.moneyParam1Amount!))
            order.inventoryManagementCost = Money.of(Currency.getInstance(model.moneyParam2Currency!), Decimal(model.moneyParam2Amount!))
            return order

        case "BuyRawMaterial":
            let order = BuyRawMaterialOrder()
            order.id = model.id
            order.executionMonth = YearMonth.parse(model.executionMonth)
            order.isExecuted = model.isExecuted
            order.amount = model.intParam1!
            order.unitPrice = Money.of(Currency.getInstance(model.amountCurrency!), Decimal(model.amountValue!))
            return order

        case "EnterMarket":
            let order = EnterMarketOrder()
            order.id = model.id
            order.executionMonth = YearMonth.parse(model.executionMonth)
            order.isExecuted = model.isExecuted
            order.intentedProductSale = model.intParam1!
            order.offeredPrice = Money.of(Currency.getInstance(model.amountCurrency!), Decimal(model.amountValue!))
            order.marketEntryCost = Money.of(Currency.getInstance(model.moneyParam1Currency!), Decimal(model.moneyParam1Amount!))
            if let msId = model.marketSimulationId {
                order.marketSimulation = marketSimulationCache[msId]
            }
            return order

        case "ChangeAmountAndPrice":
            let order = ChangeAmountAndPriceOrder()
            order.id = model.id
            order.executionMonth = YearMonth.parse(model.executionMonth)
            order.isExecuted = model.isExecuted
            order.intentedSales = model.intParam1!
            order.offeredPrice = Money.of(Currency.getInstance(model.amountCurrency!), Decimal(model.amountValue!))
            if let marketId = model.marketId {
                order.market = marketCache[marketId]
            }
            return order

        case "IncreaseQuality":
            let order = IncreaseQualityOrder()
            order.id = model.id
            order.executionMonth = YearMonth.parse(model.executionMonth)
            order.isExecuted = model.isExecuted
            order.increaseQualityAmount = Money.of(Currency.getInstance(model.amountCurrency!), Decimal(model.amountValue!))
            return order

        case "IncreaseProductivity":
            let order = IncreaseProductivityOrder()
            order.id = model.id
            order.executionMonth = YearMonth.parse(model.executionMonth)
            order.isExecuted = model.isExecuted
            order.increaseProductivityAmount = Money.of(Currency.getInstance(model.amountCurrency!), Decimal(model.amountValue!))
            return order

        case "MarketingCampaign":
            let order = MarketingCampaignOrder()
            order.id = model.id
            order.executionMonth = YearMonth.parse(model.executionMonth)
            order.isExecuted = model.isExecuted
            order.marketingCampaignAmount = Money.of(Currency.getInstance(model.amountCurrency!), Decimal(model.amountValue!))
            return order

        default:
            fatalError("Unknown order type: \(model.orderType)")
        }
    }

    // MARK: - Helpers

    private func findCompanySimulationStep(_ id: Int64, in simulation: Simulation) -> CompanySimulationStep {
        for step in simulation.simulationSteps {
            for cs in step.companySimulationSteps {
                if cs.id == id {
                    return cs
                }
            }
        }
        fatalError("CompanySimulationStep with id \(id) not found in simulation graph")
    }

    private func mapSettings(_ model: EpocSettingsModel, entries: [EpocSettingModel]) -> EpocSettings {
        let settings = EpocSettings()
        settings.id = model.id
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

    private func mapMarket(_ model: MarketModel) -> Market {
        let market = Market()
        market.id = model.id
        market.name = model.name
        market.laborForce = model.laborForce
        market.lifeExpectancy = Decimal(model.lifeExpectancy ?? 80.0)
        if let amount = model.gdpAmount, let currency = model.gdpCurrency {
            market.gdp = Money.of(Currency.getInstance(currency), Decimal(amount))
        }
        if let amount = model.gdpPppAmount, let currency = model.gdpPppCurrency {
            market.gdpPpp = Money.of(Currency.getInstance(currency), Decimal(amount))
        }
        if let growth = model.gdpGrowth {
            market.gdpGrowth = Percent.of(growth)
        }
        if let unemployment = model.unemployment {
            market.unemployment = Percent.of(unemployment)
        }
        if let amount = model.costToEnterMarketAmount, let currency = model.costToEnterMarketCurrency {
            market.costToEnterMarket = Money.of(Currency.getInstance(currency), Decimal(amount))
        }
        if let amount = model.distributionCostAmount, let currency = model.distributionCostCurrency {
            market.distributionCost = Money.of(Currency.getInstance(currency), Decimal(amount))
        }
        market.ageTo14Male = model.ageTo14Male
        market.ageTo14Female = model.ageTo14Female
        market.ageTo24Male = model.ageTo24Male
        market.ageTo24Female = model.ageTo24Female
        market.ageTo54Male = model.ageTo54Male
        market.ageTo54Female = model.ageTo54Female
        market.ageTo64Male = model.ageTo64Male
        market.ageTo64Female = model.ageTo64Female
        market.age65olderMale = model.age65olderMale
        market.age65olderFemale = model.age65olderFemale
        return market
    }
}
