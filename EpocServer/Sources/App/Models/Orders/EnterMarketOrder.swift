import Foundation

public final class EnterMarketOrder: SimulationOrder {
    public var id: Int64?
    public var executionMonth: YearMonth = .of(2020, 1)
    public var isExecuted: Bool = false
    public weak var company: Company!
    public var marketSimulation: MarketSimulation!
    public var intentedProductSale: Int = 1000
    public var offeredPrice: Money = Money.of("CHF", 800)
    public var marketEntryCost: Money = Money.of("CHF", 400000)

    public var sortOrder: Int { 5 }
    public var type: String { "Enter market" }

    public func getAmount() -> Money { marketEntryCost }

    public func execute() {
        if company.accounting.checkFunds(marketEntryCost, valueDate: executionMonth.atEndOfMonth()) {
            addDistributionInMarket()
            book(bookingDate: executionMonth.atDay(1), bookingText: "Entry into market",
                 debitAccount: FinancialAccounting.SERVICES, creditAccount: FinancialAccounting.BANK, amount: marketEntryCost)
            addMessage(.information, "MarketEntrySuccess", marketSimulation.market.name)
            isExecuted = true
        } else {
            addMessage(.warning, "NoMarketDueToFunding", marketSimulation.market.name)
        }
    }

    private func addDistributionInMarket() {
        let distribution = DistributionInMarket()
        distribution.offeredPrice = offeredPrice
        distribution.intentedProductSale = intentedProductSale
        marketSimulation.addDistributionInMarket(distribution)
        company.addDistributionInMarket(distribution)
    }
}
