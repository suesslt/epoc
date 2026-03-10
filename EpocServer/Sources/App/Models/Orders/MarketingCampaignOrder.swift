import Foundation
import Score

public final class MarketingCampaignOrder: SimulationOrder {
    public var id: Int64?
    public var executionMonth: YearMonth = .of(2020, 1)
    public var isExecuted: Bool = false
    public weak var company: Company!
    public var marketingCampaignAmount: Money = Money.of("CHF", 0)

    public var sortOrder: Int { 8 }
    public var type: String { "Marketing campaign" }

    public func getAmount() -> Money { marketingCampaignAmount }

    public func execute() {
        if company.accounting.checkFunds(marketingCampaignAmount, valueDate: executionMonth.atEndOfMonth()) {
            runMarketingCampaign()
            book(bookingDate: executionMonth.atDay(1), bookingText: "Marketing campaign",
                 debitAccount: FinancialAccounting.SERVICES, creditAccount: FinancialAccounting.BANK, amount: marketingCampaignAmount)
            addMessage(.information, "MarketingCampaign", executionMonth, marketingCampaignAmount)
            isExecuted = true
        } else {
            addMessage(.warning, "NoMarketingCampaignDueToFunding", executionMonth, marketingCampaignAmount, company.accounting.getBankBalance(executionMonth.atEndOfMonth()))
        }
    }

    private func runMarketingCampaign() {
        let pricePerCampaign = company.simulation.settings!.getPricePerMarketingCampaign()
        var increase = marketingCampaignAmount.divide(pricePerCampaign).doubleValue
        increase /= 100
        company.marketingFactor += increase
    }
}
