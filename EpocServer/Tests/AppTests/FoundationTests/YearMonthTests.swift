import XCTest
@testable import App

final class YearMonthTests: XCTestCase {
    func testCreation() {
        let ym = YearMonth.of(2020, 1)
        XCTAssertEqual(ym.year, 2020)
        XCTAssertEqual(ym.month, 1)
    }

    func testParse() {
        let ym = YearMonth.parse("2020-01")
        XCTAssertEqual(ym, YearMonth.of(2020, 1))
    }

    func testDescription() {
        XCTAssertEqual(YearMonth.of(2020, 1).description, "2020-01")
        XCTAssertEqual(YearMonth.of(2020, 12).description, "2020-12")
    }

    func testComparable() {
        XCTAssertTrue(YearMonth.of(2020, 1) < YearMonth.of(2020, 2))
        XCTAssertTrue(YearMonth.of(2019, 12) < YearMonth.of(2020, 1))
        XCTAssertFalse(YearMonth.of(2020, 6) < YearMonth.of(2020, 3))
    }

    func testPlusMonths() {
        XCTAssertEqual(YearMonth.of(2020, 1).plusMonths(1), YearMonth.of(2020, 2))
        XCTAssertEqual(YearMonth.of(2020, 11).plusMonths(2), YearMonth.of(2021, 1))
        XCTAssertEqual(YearMonth.of(2020, 1).plusMonths(12), YearMonth.of(2021, 1))
        XCTAssertEqual(YearMonth.of(2020, 6).plusMonths(0), YearMonth.of(2020, 6))
    }

    func testMonthDiff() {
        XCTAssertEqual(YearMonth.monthDiff(end: .of(2020, 6), start: .of(2020, 1)), 5)
        XCTAssertEqual(YearMonth.monthDiff(end: .of(2021, 1), start: .of(2020, 1)), 12)
        XCTAssertEqual(YearMonth.monthDiff(end: .of(2020, 1), start: .of(2020, 1)), 0)
    }

    func testLengthOfMonth() {
        XCTAssertEqual(YearMonth.of(2020, 1).lengthOfMonth, 31) // January
        XCTAssertEqual(YearMonth.of(2020, 2).lengthOfMonth, 29) // Leap year
        XCTAssertEqual(YearMonth.of(2021, 2).lengthOfMonth, 28) // Non-leap year
        XCTAssertEqual(YearMonth.of(2020, 4).lengthOfMonth, 30) // April
    }

    func testDaysInMonth() {
        let days = YearMonth.of(2020, 1).daysInMonth()
        XCTAssertEqual(days.count, 31)
    }

    func testAtEndOfMonth() {
        let date = YearMonth.of(2020, 12).atEndOfMonth()
        let calendar = Calendar(identifier: .gregorian)
        XCTAssertEqual(calendar.component(.day, from: date), 31)
        XCTAssertEqual(calendar.component(.month, from: date), 12)
        XCTAssertEqual(calendar.component(.year, from: date), 2020)
    }

    func testIsBefore() {
        XCTAssertTrue(YearMonth.of(2020, 1).isBefore(.of(2020, 2)))
        XCTAssertFalse(YearMonth.of(2020, 2).isBefore(.of(2020, 1)))
    }

    func testEquatable() {
        XCTAssertEqual(YearMonth.of(2020, 1), YearMonth.of(2020, 1))
        XCTAssertNotEqual(YearMonth.of(2020, 1), YearMonth.of(2020, 2))
    }

    func testHashable() {
        var set = Set<YearMonth>()
        set.insert(.of(2020, 1))
        set.insert(.of(2020, 1))
        XCTAssertEqual(set.count, 1)
    }
}
