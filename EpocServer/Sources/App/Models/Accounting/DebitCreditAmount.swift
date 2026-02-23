/// A single debit-credit booking instruction. Equivalent to `com.jore.epoc.bo.accounting.DebitCreditAmount`.
public struct DebitCreditAmount: Sendable {
    public let debitAccountNumber: String
    public let creditAccountNumber: String
    public let amount: Money

    public init(debit: String, credit: String, amount: Money) {
        self.debitAccountNumber = debit
        self.creditAccountNumber = credit
        self.amount = amount
    }
}
