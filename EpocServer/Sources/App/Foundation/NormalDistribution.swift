import Foundation

/// Numerical approximation of the standard normal distribution (CDF).
/// Equivalent to `com.jore.util.NormalDistribution`.
///
/// Uses Simpson's rule for numerical integration of the Gaussian function.
public struct NormalDistribution: Sendable {
    private static let leftLimit: Double = -10
    private static let n: Double = 100

    public init() {}

    /// Returns the cumulative distribution function value for x.
    /// This is the probability P(X <= x) for a standard normal distribution.
    public func distributionFor(_ x: Double) -> Double {
        guard x > Self.leftLimit else { return 0 }
        let h = Swift.abs(Self.leftLimit - x) / Self.n
        var result: Double = 0
        // Simpson's rule
        for i in 1..<Int(Self.n) {
            result += gauss(Self.leftLimit + Double(i) * h)
        }
        for i in 1...Int(Self.n) {
            result += 2 * gauss((Self.leftLimit + (Double(i) - 1) * h + (Self.leftLimit + Double(i) * h)) / 2)
        }
        result = (h / 3) * (0.5 * gauss(Self.leftLimit) + result + 0.5 * gauss(x))
        return result
    }

    /// The Gaussian (normal) probability density function.
    private func gauss(_ x: Double) -> Double {
        1.0 / sqrt(2.0 * .pi) * exp(-pow(x, 2) / 2.0)
    }
}
