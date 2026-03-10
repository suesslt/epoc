import Foundation
import Score

/// A company in the simulation, managed by a team of players.
/// Equivalent to `com.jore.epoc.bo.Company`.
public final class Company {
    private static let ONE_TWELFTH = 12

    public var id: Int64?
    public var name: String = ""
    public weak var simulation: Simulation!
    public var accounting: FinancialAccounting = FinancialAccounting()
    public var factories: [Factory] = []
    public var storages: [Storage] = []
    public var distributionInMarkets: [DistributionInMarket] = []
    public var companySimulationSteps: [CompanySimulationStep] = []
    public var simulationOrders: [any SimulationOrder] = []
    public var messages: [Message] = []

    // Factors
    public var qualityFactor: Double = 1.0
    public var marketingFactor: Double = 1.0
    public var productivityFactor: Double = 1.0

    // MARK: - Add Methods

    public func addCompanySimulationStep(_ companySimulationStep: CompanySimulationStep) {
        companySimulationStep.company = self
        companySimulationSteps.append(companySimulationStep)
    }

    public func addDistributionInMarket(_ distributionInMarket: DistributionInMarket) {
        distributionInMarket.company = self
        distributionInMarkets.append(distributionInMarket)
    }

    public func addFactory(_ factory: Factory) {
        factory.company = self
        factories.append(factory)
    }

    public func addMessage(_ message: Message) {
        message.company = self
        messages.append(message)
    }

    public func addSimulationOrder(_ simulationOrder: any SimulationOrder) {
        precondition(simulationOrder.executionMonth != YearMonth.of(1, 1), "Execution month in simulation order must not be null")
        var order = simulationOrder
        order.company = self
        simulationOrders.append(order)
    }

    public func addStorage(_ storage: Storage) {
        storage.company = self
        storages.append(storage)
    }

    // MARK: - Monthly Charges

    public func chargeBuildingMaintenanceCost(_ simulationMonth: YearMonth) {
        var nrOfBuildings = 1 // Main Building
        nrOfBuildings += factories.count
        nrOfBuildings += storages.count
        let buildingCosts = simulation.getBuildingMaintenanceCost().multiply(nrOfBuildings).divide(12)
        accounting.book(
            String(format: Messages.getMessage("Company.1", [nrOfBuildings])),
            bookingDate: simulationMonth.atDay(1),
            valueDate: simulationMonth.atDay(1),
            DebitCreditAmount(debit: FinancialAccounting.RAUMAUFWAND, credit: FinancialAccounting.BANK, amount: buildingCosts)
        )
    }

    public func chargeDepreciation(_ simulationMonth: YearMonth) {
        let realEstateBalance = accounting.getRealEstateBalance(simulationMonth.atEndOfMonth())
        let depreciation = realEstateBalance.multiply(simulation.getDepreciationRate()).divide(12)
        accounting.book(
            String(format: Messages.getMessage("Company.2", [simulation.getDepreciationRate(), realEstateBalance])),
            bookingDate: simulationMonth.atDay(1),
            valueDate: simulationMonth.atDay(1),
            DebitCreditAmount(debit: FinancialAccounting.DEPRECIATION, credit: FinancialAccounting.REAL_ESTATE, amount: depreciation)
        )
    }

    public func chargeInterest(_ simulationMonth: YearMonth) {
        let interestAmount = accounting.getLongTermDebt(simulationMonth.atEndOfMonth()).negate().multiply(simulation.getInterestRate()).divide(12)
        accounting.book(
            String(format: Messages.getMessage("Company.3", [simulation.getInterestRate(), accounting.getLongTermDebt(simulationMonth.atDay(1))])),
            bookingDate: simulationMonth.atDay(1),
            valueDate: simulationMonth.atDay(1),
            DebitCreditAmount(debit: FinancialAccounting.INTEREST, credit: FinancialAccounting.BANK, amount: interestAmount)
        )
    }

    public func chargeWorkforceCost(_ simulationMonth: YearMonth) {
        let headquarterCost = simulation.getHeadquarterCost()
        let distributionCost = distributionInMarkets.compactMap { $0.getDistributionCost() }.reduce(nil) { Money.add($0, $1) }
        let inventoryManagementCost = storages.map { $0.inventoryManagementCost }.reduce(nil) { Money.add($0, $1) }
        let productionCost = factories.map { $0.getProductionCost() }.reduce(nil) { Money.add($0, $1) }
        var workforceCost = headquarterCost
        workforceCost = Money.add(workforceCost, distributionCost)!
        workforceCost = Money.add(workforceCost, inventoryManagementCost)!
        workforceCost = Money.add(workforceCost, productionCost)!
        workforceCost = workforceCost.divide(Company.ONE_TWELFTH)
        accounting.book(
            String(format: Messages.getMessage("Company.4", [headquarterCost, distributionCost as Any, inventoryManagementCost as Any, productionCost as Any])),
            bookingDate: simulationMonth.atDay(1),
            valueDate: simulationMonth.atDay(1),
            DebitCreditAmount(debit: FinancialAccounting.SALARIES, credit: FinancialAccounting.BANK, amount: workforceCost)
        )
    }

    // MARK: - Factors

    public func discountFactors() {
        let discountRate = simulation.settings!.getFactorDiscountRate()
        qualityFactor = PercentDiscountFactor(discountRate: discountRate).discount(qualityFactor)
        marketingFactor = PercentDiscountFactor(discountRate: discountRate).discount(marketingFactor)
        productivityFactor = PercentDiscountFactor(discountRate: discountRate).discount(productivityFactor)
    }

    // MARK: - Orders

    public func getOrdersForExecutionIn(_ simulationMonth: YearMonth) -> [any SimulationOrder] {
        simulationOrders
            .filter { $0.executionMonth == simulationMonth && !$0.isExecuted }
            .sorted { $0.sortOrder < $1.sortOrder }
    }

    // MARK: - Production & Sales

    public func getSoldProducts() -> Int {
        distributionInMarkets.reduce(0) { $0 + $1.getSoldProducts() }
    }

    public func manufactureProducts(_ productionMonth: YearMonth) -> Int {
        var totalAmountProduced = 0
        var rawMaterialInStorage = storages.reduce(0) { $0 + $1.storedRawMaterials }
        if rawMaterialInStorage > 0 {
            for factory in factories {
                guard rawMaterialInStorage > 0 else { break }
                let amountProduced = factory.produce(rawMaterialInStorage, month: productionMonth, productivityFactor: productivityFactor)
                rawMaterialInStorage -= amountProduced
                totalAmountProduced += amountProduced
            }
            let averageRawMaterialPrice = Storage.getAverageRawMaterialPrice(storages)!
            accounting.book(
                Messages.getMessage("Company.6", []),
                bookingDate: productionMonth.atEndOfMonth(),
                valueDate: productionMonth.atEndOfMonth(),
                DebitCreditAmount(debit: FinancialAccounting.BESTANDESAENDERUNGEN_ROHWAREN, credit: FinancialAccounting.RAW_MATERIALS, amount: averageRawMaterialPrice.multiply(totalAmountProduced))
            )
            accounting.book(
                Messages.getMessage("Company.7", []),
                bookingDate: productionMonth.atEndOfMonth(),
                valueDate: productionMonth.atEndOfMonth(),
                DebitCreditAmount(debit: FinancialAccounting.PRODUCTS, credit: FinancialAccounting.BESTANDESAENDERUNGEN_PRODUKTE, amount: simulation.getProductionCost().multiply(totalAmountProduced))
            )
            Storage.removeRawMaterialFromStorages(storages, rawMaterialToRemove: totalAmountProduced)
            Storage.distributeProductAcrossStorages(storages, productsToStore: totalAmountProduced, month: productionMonth)
        }
        return totalAmountProduced
    }

    public func sellMaximumOf(_ distributionInMarket: DistributionInMarket, simulationMonth: YearMonth, maximumToSell: Int, offeredPrice: Money) {
        let storedAmount = storages.reduce(0) { $0 + $1.storedProducts }
        let intentedProductSale = distributionInMarket.getIntentedProductSale(simulationMonth: simulationMonth) ?? 0
        let amountToSell = min(min(storedAmount, intentedProductSale), maximumToSell)
        if amountToSell > 0 {
            distributionInMarket.setSoldProducts(simulationMonth, soldProducts: amountToSell)
            Storage.removeProductsFromStorages(storages, productsToRemove: amountToSell)
            accounting.book(
                String(format: Messages.getMessage("Company.8", [amountToSell])),
                bookingDate: simulationMonth.atEndOfMonth(),
                valueDate: simulationMonth.atEndOfMonth(),
                DebitCreditAmount(debit: FinancialAccounting.BANK, credit: FinancialAccounting.PRODUCT_REVENUES, amount: offeredPrice.multiply(amountToSell)),
                DebitCreditAmount(debit: FinancialAccounting.BESTANDESAENDERUNGEN_PRODUKTE, credit: FinancialAccounting.PRODUCTS, amount: simulation.getProductionCost().multiply(amountToSell))
            )
        }
    }

    public func setBaseCurrency(_ baseCurrency: Currency) {
        accounting.baseCurrency = baseCurrency
    }
}
