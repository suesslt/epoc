import Foundation

/// Utility for working day calculations. Equivalent to `com.jore.epoc.bo.EpocCalendar`.
public struct EpocCalendar: Sendable {
    public static let shared = EpocCalendar()

    private let calendar: Calendar = {
        var cal = Calendar(identifier: .gregorian)
        cal.timeZone = TimeZone(identifier: "UTC")!
        return cal
    }()

    /// Returns true if the given date is a working day (not Saturday or Sunday).
    public func isWorkingDay(_ date: Date) -> Bool {
        let weekday = calendar.component(.weekday, from: date)
        // In Calendar: 1 = Sunday, 7 = Saturday
        return weekday != 1 && weekday != 7
    }

    /// Returns all working days in the given YearMonth.
    public func workingDays(in yearMonth: YearMonth) -> [Date] {
        yearMonth.daysInMonth().filter { isWorkingDay($0) }
    }

    /// Returns the number of working days in the given YearMonth.
    public func workingDayCount(in yearMonth: YearMonth) -> Int {
        workingDays(in: yearMonth).count
    }
}
