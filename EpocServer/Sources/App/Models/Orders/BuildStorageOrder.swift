import Foundation

public final class BuildStorageOrder: SimulationOrder {
    public var id: Int64?
    public var executionMonth: YearMonth = .of(2020, 1)
    public var isExecuted: Bool = false
    public weak var company: Company!
    public var capacity: Int = 0
    public var timeToBuild: Int = 0
    public var constructionCost: Money = Money.of("CHF", 1000000)
    public var constructionCostPerUnit: Money = Money.of("CHF", 100)
    public var inventoryManagementCost: Money = Money.of("CHF", 500000)

    public var sortOrder: Int { 2 }
    public var type: String { "Build Storage" }

    public func getAmount() -> Money {
        constructionCost.add(constructionCostPerUnit.multiply(capacity))
    }

    public func execute() {
        if company.accounting.checkFunds(getAmount(), valueDate: executionMonth.atEndOfMonth()) {
            addStorage()
            book(bookingDate: executionMonth.atDay(1), bookingText: "Built storage",
                 debitAccount: FinancialAccounting.REAL_ESTATE, creditAccount: FinancialAccounting.BANK,
                 amount: constructionCost.add(constructionCostPerUnit.multiply(capacity)))
            addMessage(.information, "StorageBuilt", capacity, executionMonth)
            isExecuted = true
        } else {
            addMessage(.warning, "NoStorageDueToFunds", executionMonth, getAmount(), company.accounting.getBankBalance(executionMonth.atEndOfMonth()))
        }
    }

    private func addStorage() {
        let storage = Storage()
        storage.capacity = capacity
        storage.storageStartMonth = executionMonth.plusMonths(timeToBuild)
        storage.inventoryManagementCost = inventoryManagementCost
        company.addStorage(storage)
    }
}
