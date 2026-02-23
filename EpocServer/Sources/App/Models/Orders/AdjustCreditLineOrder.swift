import Foundation

public final class AdjustCreditLineOrder: SimulationOrder {
    public var id: Int64?
    public var executionMonth: YearMonth = .of(2020, 1)
    public var isExecuted: Bool = false
    public weak var company: Company!
    public var direction: CreditEventDirection = .increase
    public var amount: Money = Money.of("CHF", 0)
    public var interestRate: Percent = Percent.of("5%")

    public var sortOrder: Int { 1 }
    public var type: String { "\(direction) Credit Line" }

    public func getAmount() -> Money { amount }

    public func execute() {
        if direction == .increase {
            book(bookingDate: executionMonth.atDay(1), bookingText: "Increase credit line by \(amount)",
                 debitAccount: FinancialAccounting.BANK, creditAccount: FinancialAccounting.LONG_TERM_DEBT, amount: amount)
            addMessage(.information, "CreditLineIncreased", amount, executionMonth)
            isExecuted = true
        } else if direction == .decrease {
            if company.accounting.checkFunds(amount, valueDate: executionMonth.atEndOfMonth()) {
                book(bookingDate: executionMonth.atDay(1), bookingText: "Decrease credit line by \(amount)",
                     debitAccount: FinancialAccounting.LONG_TERM_DEBT, creditAccount: FinancialAccounting.BANK, amount: amount)
                addMessage(.information, "CreditLineDecreased", amount, executionMonth)
                isExecuted = true
            } else {
                addMessage(.warning, "InsufficientFundToDecrase", amount, company.accounting.getBankBalance(executionMonth.atEndOfMonth()))
            }
        }
    }
}
