import Foundation
import Score

public final class ChangeAmountAndPriceOrder: SimulationOrder {
    public var id: Int64?
    public var executionMonth: YearMonth = .of(2020, 1)
    public var isExecuted: Bool = false
    public weak var company: Company!
    public var intentedSales: Int = 0
    public var offeredPrice: Money = Money.of("CHF", 800)
    public var market: Market!

    public var sortOrder: Int { 6 }
    public var type: String { "Change offered amount and price" }

    public func getAmount() -> Money { offeredPrice }

    public func execute() {
        let distribution = company.distributionInMarkets.first { $0.marketSimulation.market === market }!
        distribution.intentedProductSale = intentedSales
        distribution.offeredPrice = offeredPrice
        addMessage(.information, "AmountAndPriceChangd")
    }
}
