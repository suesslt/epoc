import XCTest
@testable import App

final class NormalDistributionTests: XCTestCase {
    let nd = NormalDistribution()

    func testDistributionAtZero() {
        // Standard normal CDF at 0 should be ~0.5
        let result = nd.distributionFor(0)
        XCTAssertEqual(result, 0.5, accuracy: 0.01)
    }

    func testDistributionAtNegativeInfinity() {
        // Should approach 0
        let result = nd.distributionFor(-10)
        XCTAssertEqual(result, 0.0, accuracy: 0.001)
    }

    func testDistributionAtPositiveInfinity() {
        // Should approach 1
        let result = nd.distributionFor(10)
        XCTAssertEqual(result, 1.0, accuracy: 0.001)
    }

    func testDistributionAtOne() {
        // CDF at 1.0 should be ~0.8413
        let result = nd.distributionFor(1.0)
        XCTAssertEqual(result, 0.8413, accuracy: 0.01)
    }

    func testDistributionAtMinusOne() {
        // CDF at -1.0 should be ~0.1587
        let result = nd.distributionFor(-1.0)
        XCTAssertEqual(result, 0.1587, accuracy: 0.01)
    }

    func testDistributionAtTwo() {
        // CDF at 2.0 should be ~0.9772
        let result = nd.distributionFor(2.0)
        XCTAssertEqual(result, 0.9772, accuracy: 0.01)
    }

    func testSymmetry() {
        // CDF(x) + CDF(-x) should be ~1.0
        let x = 1.5
        let sum = nd.distributionFor(x) + nd.distributionFor(-x)
        XCTAssertEqual(sum, 1.0, accuracy: 0.01)
    }

    func testProductLifecycleUsage() {
        // Simulate how ProductLifecycle uses NormalDistribution
        let productLifecycleDuration = 100.0
        let standardDeviationSpan = 4.6
        let tailError = 0.01072411002

        // At month 50 (midpoint), should get ~50% sold
        let months = 50
        let durationFraction = Double(months) / productLifecycleDuration
        let stdFraction = -(standardDeviationSpan / 2) + (durationFraction * standardDeviationSpan)
        var percentageSold = nd.distributionFor(stdFraction) - tailError
        percentageSold += (percentageSold * tailError * 2)
        XCTAssertEqual(percentageSold, 0.5, accuracy: 0.05)
    }
}
