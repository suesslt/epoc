import XCTest
@testable import App

final class EpocCalendarTests: XCTestCase {
    let calendar = EpocCalendar.shared

    func testWeekdayIsWorkingDay() {
        // 2020-01-06 is a Monday
        let monday = YearMonth.of(2020, 1).atDay(6)
        XCTAssertTrue(calendar.isWorkingDay(monday))
    }

    func testSaturdayIsNotWorkingDay() {
        // 2020-01-04 is a Saturday
        let saturday = YearMonth.of(2020, 1).atDay(4)
        XCTAssertFalse(calendar.isWorkingDay(saturday))
    }

    func testSundayIsNotWorkingDay() {
        // 2020-01-05 is a Sunday
        let sunday = YearMonth.of(2020, 1).atDay(5)
        XCTAssertFalse(calendar.isWorkingDay(sunday))
    }

    func testWorkingDaysInJanuary2020() {
        // January 2020 has 23 working days
        let count = calendar.workingDayCount(in: .of(2020, 1))
        XCTAssertEqual(count, 23)
    }

    func testWorkingDaysInFebruary2020() {
        // February 2020 (leap year) has 29 days, 20 working days
        let count = calendar.workingDayCount(in: .of(2020, 2))
        XCTAssertEqual(count, 20)
    }
}
