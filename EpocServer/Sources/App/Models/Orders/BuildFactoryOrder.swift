import Foundation

public final class BuildFactoryOrder: SimulationOrder {
    public var id: Int64?
    public var executionMonth: YearMonth = .of(2020, 1)
    public var isExecuted: Bool = false
    public weak var company: Company!
    public var productionLines: Int = 10
    public var timeToBuild: Int = 0
    public var dailyCapacityPerProductionLine: Int = 4
    public var productionLineLaborCost: Money = Money.of("CHF", 500000)
    public var constructionCost: Money = Money.of("CHF", 1000000)
    public var constructionCostPerLine: Money = Money.of("CHF", 100000)

    public var sortOrder: Int { 4 }
    public var type: String { "Build Factory" }

    public func getAmount() -> Money {
        constructionCost.add(constructionCostPerLine.multiply(productionLines))
    }

    public func execute() {
        if company.accounting.checkFunds(getAmount(), valueDate: executionMonth.atEndOfMonth()) {
            addFactory()
            book(bookingDate: executionMonth.atDay(1), bookingText: "Built factory",
                 debitAccount: FinancialAccounting.REAL_ESTATE, creditAccount: FinancialAccounting.BANK,
                 amount: constructionCost.add(constructionCostPerLine.multiply(productionLines)))
            addMessage(.information, "FactoryCreated", getAmount(), executionMonth)
            isExecuted = true
        } else {
            addMessage(.warning, "NoFactoryDueToFunds", executionMonth, getAmount(), company.accounting.getBankBalance(executionMonth.atEndOfMonth()))
        }
    }

    private func addFactory() {
        let factory = Factory()
        factory.productionLines = productionLines
        factory.productionStartMonth = executionMonth.plusMonths(timeToBuild)
        factory.dailyCapacityPerProductionLine = dailyCapacityPerProductionLine
        factory.productionLineLaborCost = productionLineLaborCost
        company.addFactory(factory)
    }
}
