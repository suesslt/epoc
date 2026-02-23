import Foundation

/// Models the S-curve product adoption over time using a normal distribution.
/// Equivalent to `com.jore.epoc.bo.ProductLifecycle`.
///
/// A new product reaches full market penetration gradually. This class returns
/// the percentage of the market reachable at a given number of months since launch.
public struct ProductLifecycle: Sendable {
    private static let normalDistribution = NormalDistribution()
    private static let standardDeviationSpan = 4.6
    private static let tailError = 0.01072411002

    private let productLifecycleDuration: Double

    public init(_ duration: Double) {
        self.productLifecycleDuration = duration
    }

    /// Returns the percentage of the market sold/reachable after the given number of months.
    /// Returns a value between 0 and ~1.0 (100%).
    public func getPercentageSoldForMonths(_ months: Int) -> Double {
        precondition(productLifecycleDuration > 0, "Duration not initialized")
        let durationFraction = Double(months) / productLifecycleDuration
        let standardDeviationFraction = -(Self.standardDeviationSpan / 2) + (durationFraction * Self.standardDeviationSpan)
        var percentageSold = Self.normalDistribution.distributionFor(standardDeviationFraction) - Self.tailError
        percentageSold += (percentageSold * Self.tailError * 2)
        return max(min(percentageSold, 100), 0)
    }
}
