import Foundation

/// Linear demand curve between two price-demand points.
/// Equivalent to `com.jore.epoc.bo.DemandCurve`.
///
/// Given a higher price (low demand) and lower price (high demand), calculates
/// the percentage of the market willing to buy at any given price point.
public struct DemandCurve: Sendable {
    private let higherPrice: Money
    private let higherPricePercent: Percent
    private let lowerPrice: Money
    private let lowerPricePercent: Percent

    public init(higherPrice: Money, higherPricePercent: Percent, lowerPrice: Money, lowerPricePercent: Percent) {
        precondition(lowerPrice < higherPrice, "Lower price must be lower than higher price")
        self.higherPrice = higherPrice
        self.higherPricePercent = higherPricePercent
        self.lowerPrice = lowerPrice
        self.lowerPricePercent = lowerPricePercent
    }

    public static func create(_ currency: String, _ higherPrice: Double, _ higherPricePercent: String,
                               _ lowerPrice: Double, _ lowerPricePercent: String) -> DemandCurve {
        DemandCurve(
            higherPrice: Money.of(currency, higherPrice),
            higherPricePercent: Percent.of(higherPricePercent),
            lowerPrice: Money.of(currency, lowerPrice),
            lowerPricePercent: Percent.of(lowerPricePercent)
        )
    }

    /// Calculates the percentage of the market willing to buy at the given price.
    /// Uses linear interpolation: demand% = a * price + b
    /// Result is clamped between 0% and the maximum (b value).
    public func getDemandForPrice(_ price: Money) -> Percent {
        let priceDifference = higherPrice.subtract(lowerPrice)
        let percentDifference = higherPricePercent.subtract(lowerPricePercent)
        // a = percentDifference / priceDifference
        let aValue = percentDifference.factorAmount.doubleValue / priceDifference.amount.doubleValue
        // b = lowerPercent - a * lowerPrice
        let bValue = lowerPricePercent.factorAmount.doubleValue - aValue * lowerPrice.amount.doubleValue
        // demand = a * price + b, clamped to [0, b]
        let demand = aValue * price.amount.doubleValue + bValue
        let clamped = min(bValue, max(0, demand))
        // Round to Percent scale (6 decimal places)
        let rounded = (clamped * 1_000_000).rounded() / 1_000_000
        return Percent.of(rounded)
    }
}
