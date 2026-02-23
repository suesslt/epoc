import Foundation

public final class IncreaseQualityOrder: SimulationOrder {
    public var id: Int64?
    public var executionMonth: YearMonth = .of(2020, 1)
    public var isExecuted: Bool = false
    public weak var company: Company!
    public var increaseQualityAmount: Money = Money.of("CHF", 0)

    public var sortOrder: Int { 7 }
    public var type: String { "Increase quality" }

    public func getAmount() -> Money { increaseQualityAmount }

    public func execute() {
        if company.accounting.checkFunds(increaseQualityAmount, valueDate: executionMonth.atEndOfMonth()) {
            increaseQuality()
            book(bookingDate: executionMonth.atDay(1), bookingText: "Increase quality",
                 debitAccount: FinancialAccounting.SERVICES, creditAccount: FinancialAccounting.BANK, amount: increaseQualityAmount)
            addMessage(.information, "IncreaseQuality", executionMonth, increaseQualityAmount)
            isExecuted = true
        } else {
            addMessage(.warning, "NoIncreaseInQualityDueToFunding", executionMonth, increaseQualityAmount, company.accounting.getBankBalance(executionMonth.atEndOfMonth()))
        }
    }

    private func increaseQuality() {
        let pricePerPercentPoint = company.simulation.settings!.getPricePerPercentPointQuality()
        var increase = increaseQualityAmount.divide(pricePerPercentPoint).doubleValue
        increase /= 100
        company.qualityFactor += increase
    }
}
