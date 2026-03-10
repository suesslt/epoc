import Foundation
import Score

/// Protocol for all simulation orders (player actions).
/// Equivalent to `com.jore.epoc.bo.orders.SimulationOrder` + `AbstractSimulationOrder`.
public protocol SimulationOrder: AnyObject {
    var id: Int64? { get set }
    var executionMonth: YearMonth { get set }
    var isExecuted: Bool { get set }
    var company: Company! { get set }

    func execute()
    func getAmount() -> Money
    var sortOrder: Int { get }
    var type: String { get }
}

extension SimulationOrder {
    static var firstOfMonth: Int { 1 }

    func addMessage(_ level: MessageLevel, _ key: String, _ params: Any...) {
        let msg = Message()
        msg.relevantMonth = executionMonth
        msg.level = level
        msg.message = Messages.getMessage(key, params)
        company.addMessage(msg)
    }

    func book(bookingDate: Date, bookingText: String, debitAccount: String, creditAccount: String, amount: Money) {
        company.accounting.book(bookingText, bookingDate: bookingDate, valueDate: bookingDate,
                                DebitCreditAmount(debit: debitAccount, credit: creditAccount, amount: amount))
    }

    func book(bookingDate: Date, bookingText: String,
              debitAccount1: String, creditAccount1: String, amount1: Money,
              debitAccount2: String, creditAccount2: String, amount2: Money) {
        company.accounting.book(bookingText, bookingDate: bookingDate, valueDate: bookingDate,
                                DebitCreditAmount(debit: debitAccount1, credit: creditAccount1, amount: amount1),
                                DebitCreditAmount(debit: debitAccount2, credit: creditAccount2, amount: amount2))
    }
}
