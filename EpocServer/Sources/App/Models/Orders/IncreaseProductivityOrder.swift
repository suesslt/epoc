import Foundation

public final class IncreaseProductivityOrder: SimulationOrder {
    public var id: Int64?
    public var executionMonth: YearMonth = .of(2020, 1)
    public var isExecuted: Bool = false
    public weak var company: Company!
    public var increaseProductivityAmount: Money = Money.of("CHF", 0)

    public var sortOrder: Int { 7 }
    public var type: String { "Increase productivity" }

    public func getAmount() -> Money { increaseProductivityAmount }

    public func execute() {
        if company.accounting.checkFunds(increaseProductivityAmount, valueDate: executionMonth.atEndOfMonth()) {
            setProductivityFactor()
            book(bookingDate: executionMonth.atDay(1), bookingText: "Increase productivity",
                 debitAccount: FinancialAccounting.SERVICES, creditAccount: FinancialAccounting.BANK, amount: increaseProductivityAmount)
            addMessage(.information, "IncreaseProductivity", executionMonth, increaseProductivityAmount)
            isExecuted = true
        } else {
            addMessage(.warning, "NoIncreaseInProductivityDueToFunding", executionMonth, increaseProductivityAmount, company.accounting.getBankBalance(executionMonth.atEndOfMonth()))
        }
    }

    private func setProductivityFactor() {
        let pricePerPercentPoint = company.simulation.settings!.getPricePerPercentPointProductivity()
        var increase = increaseProductivityAmount.divide(pricePerPercentPoint).doubleValue
        increase /= 100
        company.productivityFactor += increase
    }
}
