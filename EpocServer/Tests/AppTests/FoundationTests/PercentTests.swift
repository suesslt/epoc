import XCTest
@testable import App

final class PercentTests: XCTestCase {

    // MARK: - Creation

    func testOfString() {
        let p = Percent.of("10%")
        XCTAssertEqual(p.doubleValue, 0.1, accuracy: 0.0001)
    }

    func testOfString80() {
        let p = Percent.of("80%")
        XCTAssertEqual(p.doubleValue, 0.8, accuracy: 0.0001)
    }

    func testOfString5() {
        let p = Percent.of("5%")
        XCTAssertEqual(p.doubleValue, 0.05, accuracy: 0.0001)
    }

    func testOfDouble() {
        let p = Percent.of(0.15)
        XCTAssertEqual(p.doubleValue, 0.15, accuracy: 0.0001)
    }

    func testHundred() {
        XCTAssertEqual(Percent.hundred.doubleValue, 1.0, accuracy: 0.0001)
    }

    func testZero() {
        XCTAssertEqual(Percent.zero.doubleValue, 0.0, accuracy: 0.0001)
    }

    // MARK: - Arithmetic

    func testAdd() {
        let result = Percent.of("20%").add(Percent.of("30%"))
        XCTAssertEqual(result.doubleValue, 0.5, accuracy: 0.0001)
    }

    func testSubtract() {
        let result = Percent.of("80%").subtract(Percent.of("20%"))
        XCTAssertEqual(result.doubleValue, 0.6, accuracy: 0.0001)
    }

    func testNegate() {
        let result = Percent.of("10%").negate()
        XCTAssertEqual(result.doubleValue, -0.1, accuracy: 0.0001)
    }

    func testApplyTo() {
        let result = Percent.of("50%").applyTo(1000)
        XCTAssertEqual(result, 500)
    }

    func testDiscount() {
        let result = Percent.of("10%").discount(1000)
        XCTAssertEqual(result, 900)
    }

    // MARK: - Comparison

    func testEquality() {
        XCTAssertEqual(Percent.of("10%"), Percent.of("10%"))
        XCTAssertNotEqual(Percent.of("10%"), Percent.of("20%"))
    }

    func testComparable() {
        XCTAssertTrue(Percent.of("10%") < Percent.of("20%"))
    }

    // MARK: - Description

    func testDescription() {
        XCTAssertEqual(Percent.of("10%").description, "10%")
        XCTAssertEqual(Percent.of("5%").description, "5%")
        XCTAssertEqual(Percent.of("80%").description, "80%")
    }

    // MARK: - DemandCurve-related values

    func testDemandCurvePercents() {
        // Values from DemandCurveTest
        let higher = Percent.of("20%")
        let lower = Percent.of("80%")
        let diff = lower.subtract(higher)
        XCTAssertEqual(diff.doubleValue, 0.6, accuracy: 0.0001)
    }

    func testSpecificPercent() {
        // From DemandCurveTest: Percent.of("54.2857%")
        let p = Percent.of("54.2857%")
        XCTAssertEqual(p.doubleValue, 0.542857, accuracy: 0.0001)
    }

    // MARK: - FactorAmount (BigDecimal)

    func testFactorAmount() {
        let p = Percent.of("15%")
        XCTAssertEqual(p.factorAmount.doubleValue, 0.15, accuracy: 0.0001)
    }

    func testToBigDecimal() {
        let p = Percent.of("15%")
        XCTAssertEqual(p.toBigDecimal.doubleValue, 15.0, accuracy: 0.01)
    }
}
