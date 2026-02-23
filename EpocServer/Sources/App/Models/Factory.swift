import Foundation

/// Production facility with multiple production lines.
/// Equivalent to `com.jore.epoc.bo.Factory`.
public final class Factory {
    public var id: Int64?
    public weak var company: Company!
    public var productionLines: Int = 0
    public var productionStartMonth: YearMonth = .of(2020, 1)
    public var dailyCapacityPerProductionLine: Int = 4
    public var productionLineLaborCost: Money = Money.of("CHF", 500000)

    public func getProductionCost() -> Money {
        productionLineLaborCost.multiply(productionLines)
    }

    /// Produces goods by converting raw materials from storages.
    /// Returns the number of products manufactured this month.
    public func produce(_ maximumToProduce: Int, month productionMonth: YearMonth, productivityFactor: Double) -> Int {
        precondition(dailyCapacityPerProductionLine > 0, "Capacity per production line must be greater zero.")
        var result = 0
        if isProductionReady(productionMonth) && Storage.getTotalRawMaterialStored(company.storages) > 0 {
            for date in productionMonth.daysInMonth() {
                if EpocCalendar.shared.isWorkingDay(date) {
                    let dailyCapacity = Int(Double(productionLines * dailyCapacityPerProductionLine) * productivityFactor)
                    let amountRemoved = Storage.removeRawMaterialFromStorages(company.storages, rawMaterialToRemove: dailyCapacity)
                    result += amountRemoved
                    Storage.distributeProductAcrossStorages(company.storages, productsToStore: amountRemoved, month: productionMonth)
                }
            }
        }
        return result
    }

    private func isProductionReady(_ productionMonth: YearMonth) -> Bool {
        !productionMonth.isBefore(productionStartMonth)
    }
}
