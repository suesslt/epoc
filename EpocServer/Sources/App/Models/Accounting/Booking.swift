import Foundation

/// A single debit/credit line within a journal entry.
/// Equivalent to `com.jore.epoc.bo.accounting.Booking`.
public final class Booking {
    public var id: Int64?
    public var amount: Decimal = 0
    public weak var journalEntry: JournalEntry?
    public weak var creditAccount: Account?
    public weak var debitAccount: Account?

    /// The value date comes from the parent journal entry.
    public var valueDate: Date {
        journalEntry!.valueDate
    }
}
