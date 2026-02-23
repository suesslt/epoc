import XCTest
@testable import App

final class MoneyTests: XCTestCase {

    // MARK: - Creation

    func testOfCurrencyCodeAndDouble() {
        let money = Money.of("CHF", 1000.0)
        XCTAssertEqual(money.currency, Currency.getInstance("CHF"))
        XCTAssertEqual(money.roundedAmount, 1000)
    }

    func testOfCurrencyCodeAndInt() {
        let money = Money.of("CHF", 500)
        XCTAssertEqual(money.roundedAmount, 500)
    }

    func testOfCurrencyAndDecimal() {
        let money = Money.of(Currency.getInstance("USD"), Decimal(250))
        XCTAssertEqual(money.currency, Currency.getInstance("USD"))
        XCTAssertEqual(money.roundedAmount, 250)
    }

    // MARK: - Parsing

    func testParseCurrencyFirst() {
        let money = Money.parse("CHF 1000000")
        XCTAssertEqual(money, Money.of("CHF", 1000000))
    }

    func testParseNegative() {
        let money = Money.parse("CHF -500")
        XCTAssertEqual(money, Money.of("CHF", -500))
    }

    func testParseDecimal() {
        let money = Money.parse("CHF 99.95")
        XCTAssertEqual(money, Money.of("CHF", 99.95))
    }

    // MARK: - Arithmetic

    func testAdd() {
        let result = Money.of("CHF", 100).add(Money.of("CHF", 200))
        XCTAssertEqual(result, Money.of("CHF", 300))
    }

    func testSubtract() {
        let result = Money.of("CHF", 500).subtract(Money.of("CHF", 200))
        XCTAssertEqual(result, Money.of("CHF", 300))
    }

    func testMultiplyInt() {
        let result = Money.of("CHF", 100).multiply(3)
        XCTAssertEqual(result, Money.of("CHF", 300))
    }

    func testMultiplyDouble() {
        let result = Money.of("CHF", 1000).multiply(0.5)
        XCTAssertEqual(result, Money.of("CHF", 500))
    }

    func testMultiplyPercent() {
        let result = Money.of("CHF", 1000).multiply(Percent.of("10%"))
        XCTAssertEqual(result, Money.of("CHF", 100))
    }

    func testDivideInt() {
        let result = Money.of("CHF", 1000).divide(4)
        XCTAssertEqual(result, Money.of("CHF", 250))
    }

    func testDivideDouble() {
        let result = Money.of("CHF", 1000).divide(2.0)
        XCTAssertEqual(result, Money.of("CHF", 500))
    }

    func testDivideMoney() {
        let ratio = Money.of("CHF", 1000).divide(Money.of("CHF", 500))
        XCTAssertEqual(ratio.doubleValue, 2.0, accuracy: 0.001)
    }

    func testNegate() {
        let result = Money.of("CHF", 100).negate()
        XCTAssertEqual(result, Money.of("CHF", -100))
    }

    func testNegateNegative() {
        let result = Money.of("CHF", -100).negate()
        XCTAssertEqual(result, Money.of("CHF", 100))
    }

    // MARK: - Static null-safe add

    func testStaticAddBothPresent() {
        let result = Money.add(Money.of("CHF", 100), Money.of("CHF", 200))
        XCTAssertEqual(result, Money.of("CHF", 300))
    }

    func testStaticAddFirstNil() {
        let result = Money.add(nil, Money.of("CHF", 200))
        XCTAssertEqual(result, Money.of("CHF", 200))
    }

    func testStaticAddSecondNil() {
        let result = Money.add(Money.of("CHF", 100), nil)
        XCTAssertEqual(result, Money.of("CHF", 100))
    }

    func testStaticAddBothNil() {
        let result = Money.add(nil, nil)
        XCTAssertNil(result)
    }

    // MARK: - Comparison

    func testComparable() {
        XCTAssertTrue(Money.of("CHF", 100) < Money.of("CHF", 200))
        XCTAssertFalse(Money.of("CHF", 200) < Money.of("CHF", 100))
    }

    func testEquality() {
        XCTAssertEqual(Money.of("CHF", 100), Money.of("CHF", 100))
        XCTAssertNotEqual(Money.of("CHF", 100), Money.of("CHF", 200))
        XCTAssertNotEqual(Money.of("CHF", 100), Money.of("USD", 100))
    }

    // MARK: - Description (toString)

    func testDescription() {
        let money = Money.of("CHF", 1000000)
        XCTAssertEqual(money.description, "CHF 1000000.00")
    }

    func testDescriptionNegative() {
        let money = Money.of("CHF", -500)
        XCTAssertEqual(money.description, "CHF -500.00")
    }

    func testDescriptionDecimals() {
        let money = Money.of("CHF", 99.95)
        XCTAssertEqual(money.description, "CHF 99.95")
    }

    // MARK: - Signum

    func testSignum() {
        XCTAssertEqual(Money.of("CHF", 100).signum, 1)
        XCTAssertEqual(Money.of("CHF", -100).signum, -1)
        XCTAssertEqual(Money.of("CHF", 0).signum, 0)
    }

    // MARK: - Values used in EPOC simulation

    func testSimulationValues() {
        // These values appear in FullSimulationTests
        let creditLine = Money.of("CHF", 13000000)
        XCTAssertEqual(creditLine.description, "CHF 13000000.00")

        let rawMaterialPrice = Money.of("CHF", 300)
        let total = rawMaterialPrice.multiply(1000)
        XCTAssertEqual(total, Money.of("CHF", 300000))
    }

    func testMultiplyByTwelve() {
        // Monthly to annual: factory labor cost
        let monthly = Money.of("CHF", 500000).divide(12)
        let result = monthly.multiply(12)
        XCTAssertEqual(result.roundedAmount, Money.of("CHF", 500000).roundedAmount, accuracy: Decimal(1))
    }
}

// Helper for Decimal accuracy comparison
extension XCTestCase {
    func XCTAssertEqual(_ lhs: Decimal, _ rhs: Decimal, accuracy: Decimal, file: StaticString = #filePath, line: UInt = #line) {
        let diff = abs((lhs - rhs).doubleValue)
        XCTAssertTrue(diff <= accuracy.doubleValue, "(\(lhs)) is not equal to (\(rhs)) +/- (\(accuracy))", file: file, line: line)
    }
}
