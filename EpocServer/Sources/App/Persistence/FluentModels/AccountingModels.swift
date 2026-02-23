import Fluent
import Vapor

final class FinancialAccountingModel: Model, Content, @unchecked Sendable {
    static let schema = "financial_accountings"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Field(key: "base_currency") var baseCurrency: String

    @Children(for: \.$accounting) var accounts: [AccountModel]
    @Children(for: \.$accounting) var journalEntries: [JournalEntryModel]

    init() {}
}

final class AccountModel: Model, Content, @unchecked Sendable {
    static let schema = "accounts"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Parent(key: "accounting_id") var accounting: FinancialAccountingModel
    @Field(key: "account_type") var accountType: String
    @Field(key: "number") var number: String
    @Field(key: "name") var name: String
    @Field(key: "start_balance") var startBalance: Double

    @Children(for: \.$debitAccount) var debitBookings: [BookingModel]
    @Children(for: \.$creditAccount) var creditBookings: [BookingModel]

    init() {}
}

final class JournalEntryModel: Model, Content, @unchecked Sendable {
    static let schema = "journal_entries"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Parent(key: "accounting_id") var accounting: FinancialAccountingModel
    @Field(key: "booking_text") var bookingText: String
    @Field(key: "booking_date") var bookingDate: Date
    @Field(key: "value_date") var valueDate: Date

    @Children(for: \.$journalEntry) var bookings: [BookingModel]

    init() {}
}

final class BookingModel: Model, Content, @unchecked Sendable {
    static let schema = "bookings"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Parent(key: "journal_entry_id") var journalEntry: JournalEntryModel
    @Parent(key: "debit_account_id") var debitAccount: AccountModel
    @Parent(key: "credit_account_id") var creditAccount: AccountModel
    @Field(key: "amount") var amount: Double

    init() {}
}
