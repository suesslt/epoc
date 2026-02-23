import XCTest
@testable import App

final class CurrencyTests: XCTestCase {
    func testGetInstance() {
        let chf = Currency.getInstance("CHF")
        XCTAssertEqual(chf.currencyCode, "CHF")
        XCTAssertEqual(chf.name, "Swiss Franc")
        XCTAssertEqual(chf.fractionDigits, 2)
        XCTAssertEqual(chf.numericCode, 756)
    }

    func testGetInstanceUSD() {
        let usd = Currency.getInstance("USD")
        XCTAssertEqual(usd.currencyCode, "USD")
        XCTAssertEqual(usd.name, "US Dollar")
        XCTAssertEqual(usd.fractionDigits, 2)
    }

    func testGetInstanceJPY() {
        let jpy = Currency.getInstance("JPY")
        XCTAssertEqual(jpy.fractionDigits, 0)
    }

    func testEquality() {
        XCTAssertEqual(Currency.getInstance("CHF"), Currency.getInstance("CHF"))
        XCTAssertNotEqual(Currency.getInstance("CHF"), Currency.getInstance("USD"))
    }

    func testComparable() {
        XCTAssertTrue(Currency.getInstance("CHF") < Currency.getInstance("USD"))
    }

    func testRoundingUnit() {
        XCTAssertEqual(Currency.getInstance("CHF").roundingUnit, Decimal(string: "0.01"))
        XCTAssertEqual(Currency.getInstance("JPY").roundingUnit, Decimal(1))
    }

    func testDescription() {
        XCTAssertEqual(Currency.getInstance("CHF").description, "CHF")
    }

    func testHashable() {
        var set = Set<Currency>()
        set.insert(Currency.getInstance("CHF"))
        set.insert(Currency.getInstance("CHF"))
        set.insert(Currency.getInstance("USD"))
        XCTAssertEqual(set.count, 2)
    }
}
