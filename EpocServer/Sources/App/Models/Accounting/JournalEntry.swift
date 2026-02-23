import Foundation

/// A journal entry containing one or more bookings.
/// Equivalent to `com.jore.epoc.bo.accounting.JournalEntry`.
public final class JournalEntry {
    public var id: Int64?
    public weak var accounting: FinancialAccounting?
    public var bookingText: String = ""
    public var bookingDate: Date = Date()
    public var valueDate: Date = Date()
    public var bookings: [Booking] = []

    public func addBooking(_ booking: Booking) {
        booking.journalEntry = self
        bookings.append(booking)
    }
}
