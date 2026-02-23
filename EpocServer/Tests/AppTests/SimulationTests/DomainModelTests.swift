import XCTest

/// Unit tests for DemandCurve, ProductLifecycle, and PercentDiscountFactor.
final class DomainModelTests: XCTestCase {

    // MARK: - DemandCurve Tests

    func testDemandCurveAtLowerPrice() {
        let curve = DemandCurve(
            higherPrice: Money.of("CHF", 1200),
            higherPercent: Percent.of("20%"),
            lowerPrice: Money.of("CHF", 500),
            lowerPercent: Percent.of("80%")
        )
        let demand = curve.getDemandForPrice(Money.of("CHF", 500))
        XCTAssertEqual(Percent.of("80%"), demand)
    }

    func testDemandCurveAtHigherPrice() {
        let curve = DemandCurve(
            higherPrice: Money.of("CHF", 1200),
            higherPercent: Percent.of("20%"),
            lowerPrice: Money.of("CHF", 500),
            lowerPercent: Percent.of("80%")
        )
        let demand = curve.getDemandForPrice(Money.of("CHF", 1200))
        XCTAssertEqual(Percent.of("20%"), demand)
    }

    func testDemandCurveMiddlePrice() {
        let curve = DemandCurve(
            higherPrice: Money.of("CHF", 1200),
            higherPercent: Percent.of("20%"),
            lowerPrice: Money.of("CHF", 500),
            lowerPercent: Percent.of("80%")
        )
        let demand = curve.getDemandForPrice(Money.of("CHF", 850))
        // Linear interpolation: 80% - (850-500)/(1200-500) * (80%-20%) = 80% - 30% = 50%
        XCTAssertEqual(50, demand.applyTo(100))
    }

    // MARK: - ProductLifecycle Tests

    func testProductLifecycleEarlyMonths() {
        let lifecycle = ProductLifecycle(100)
        let early = lifecycle.getPercentageSoldForMonths(1)
        XCTAssertTrue(early > 0)
        XCTAssertTrue(early < 0.1)
    }

    func testProductLifecycleMiddle() {
        let lifecycle = ProductLifecycle(100)
        let middle = lifecycle.getPercentageSoldForMonths(50)
        XCTAssertTrue(middle > 0.4)
        XCTAssertTrue(middle < 0.6)
    }

    func testProductLifecycleLate() {
        let lifecycle = ProductLifecycle(100)
        let late = lifecycle.getPercentageSoldForMonths(99)
        XCTAssertTrue(late > 0.9)
    }

    // MARK: - PercentDiscountFactor Tests

    func testPercentDiscountFactor() {
        let factor = PercentDiscountFactor(discountRate: Percent.of("10%"))
        let result = factor.discount(1.5)
        // 1 + (1.5 - 1) * (1 - 0.1) = 1 + 0.5 * 0.9 = 1.45
        XCTAssertEqual(1.45, result, accuracy: 0.001)
    }

    func testPercentDiscountFactorAtOne() {
        let factor = PercentDiscountFactor(discountRate: Percent.of("10%"))
        let result = factor.discount(1.0)
        XCTAssertEqual(1.0, result, accuracy: 0.001)
    }

    func testPercentDiscountFactorMultipleApplications() {
        let factor = PercentDiscountFactor(discountRate: Percent.of("10%"))
        var value = 1.5
        for _ in 0..<10 {
            value = factor.discount(value)
        }
        // After 10 applications, should converge toward 1.0
        XCTAssertTrue(value > 1.0)
        XCTAssertTrue(value < 1.2)
    }

    // MARK: - Market Tests

    func testMarketSizeForConsumption() {
        let market = Market()
        market.laborForce = 1000000
        XCTAssertEqual(1000000, market.getMarketSizeForConsumption())
    }

    // MARK: - Storage Tests

    func testStorageDistribution() {
        let storage = Storage()
        storage.capacity = 1000
        storage.storageStartMonth = .of(2020, 1)
        XCTAssertEqual(1000, storage.getAvailableCapacity(.of(2020, 1)))
        let stored = storage.storeRawMaterials(500, month: .of(2020, 1), unitPrice: Money.of("CHF", 300))
        XCTAssertEqual(500, stored)
        XCTAssertEqual(500, storage.storedRawMaterials)
        XCTAssertEqual(500, storage.getAvailableCapacity(.of(2020, 1)))
    }

    func testStorageNotReadyBefore() {
        let storage = Storage()
        storage.capacity = 1000
        storage.storageStartMonth = .of(2020, 3)
        XCTAssertEqual(0, storage.getAvailableCapacity(.of(2020, 2)))
        XCTAssertEqual(1000, storage.getAvailableCapacity(.of(2020, 3)))
    }
}
