import Foundation

/// Applies monthly decay to quality/marketing/productivity factors.
/// Equivalent to `com.jore.epoc.bo.PercentDiscountFactor`.
///
/// Formula: factor = 1 + (factor - 1) * (1 - discountRate)
/// E.g. a factor of 1.5 with 10% discount becomes 1.45.
public struct PercentDiscountFactor: Sendable {
    private let discountRate: Percent

    public init(discountRate: Percent) {
        self.discountRate = discountRate
    }

    /// Discounts the given factor. Factor must be >= 1.0.
    public func discount(_ factor: Double) -> Double {
        precondition(factor >= 1.0, "Factor must be greater equal one (>=1.0)")
        var result = factor
        result -= 1.0
        result *= (1.0 - discountRate.doubleValue)
        result += 1.0
        return result
    }
}
