import Foundation

/// A general ledger account with debit and credit bookings.
/// Equivalent to `com.jore.epoc.bo.accounting.Account`.
public final class Account {
    public var id: Int64?
    public weak var accounting: FinancialAccounting?
    public let number: String
    public let name: String
    public let accountType: AccountType
    public var startBalance: Decimal = 0
    public var creditBookings: [Booking] = []
    public var debitBookings: [Booking] = []

    public init(accountType: AccountType, number: String, name: String) {
        self.accountType = accountType
        self.number = number
        self.name = name
    }

    public func credit(_ booking: Booking) {
        booking.creditAccount = self
        creditBookings.append(booking)
    }

    public func debit(_ booking: Booking) {
        booking.debitAccount = self
        debitBookings.append(booking)
    }

    /// Returns the balance of this account as of the given value date.
    /// For balance sheet accounts: debit - credit
    /// For income statement accounts: credit - debit (negated)
    public func getBalance(_ valueDate: Date) -> Decimal {
        let debitSum = debitBookings
            .filter { $0.valueDate <= valueDate }
            .reduce(Decimal(0)) { $0 + $1.amount }
        let creditSum = creditBookings
            .filter { $0.valueDate <= valueDate }
            .reduce(Decimal(0)) { $0 + $1.amount }
        let result = startBalance + debitSum - creditSum
        return accountType == .balanceSheet ? result : -result
    }
}
