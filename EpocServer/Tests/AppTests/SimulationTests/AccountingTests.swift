import XCTest

/// Unit tests for the double-entry accounting system.
final class AccountingTests: XCTestCase {

    func testInitialAccounts() {
        let accounting = FinancialAccounting()
        accounting.baseCurrency = Currency.getInstance("CHF")
        let endOfJan = YearMonth.of(2020, 1).atEndOfMonth()
        XCTAssertEqual(Money.of("CHF", 0), accounting.getBankBalance(endOfJan))
        XCTAssertEqual(Money.of("CHF", 0), accounting.getLongTermDebt(endOfJan))
        XCTAssertEqual(Money.of("CHF", 0), accounting.getPnL(endOfJan))
        XCTAssertEqual(Money.of("CHF", 0), accounting.getOwnersCapital(endOfJan))
    }

    func testCreditLineIncrease() {
        let accounting = FinancialAccounting()
        accounting.baseCurrency = Currency.getInstance("CHF")
        let jan1 = YearMonth.of(2020, 1).atDay(1)
        let endOfJan = YearMonth.of(2020, 1).atEndOfMonth()
        accounting.book("Increase credit line", bookingDate: jan1, valueDate: jan1,
                        DebitCreditAmount(debit: FinancialAccounting.BANK, credit: FinancialAccounting.LONG_TERM_DEBT, amount: Money.of("CHF", 1000000)))
        XCTAssertEqual(Money.of("CHF", 1000000), accounting.getBankBalance(endOfJan))
        XCTAssertEqual(Money.of("CHF", -1000000), accounting.getLongTermDebt(endOfJan))
        XCTAssertEqual(Money.of("CHF", 0), accounting.getOwnersCapital(endOfJan))
    }

    func testCheckFunds() {
        let accounting = FinancialAccounting()
        accounting.baseCurrency = Currency.getInstance("CHF")
        let jan1 = YearMonth.of(2020, 1).atDay(1)
        let endOfJan = YearMonth.of(2020, 1).atEndOfMonth()
        accounting.book("Credit", bookingDate: jan1, valueDate: jan1,
                        DebitCreditAmount(debit: FinancialAccounting.BANK, credit: FinancialAccounting.LONG_TERM_DEBT, amount: Money.of("CHF", 5000000)))
        XCTAssertTrue(accounting.checkFunds(Money.of("CHF", 5000000), valueDate: endOfJan))
        XCTAssertFalse(accounting.checkFunds(Money.of("CHF", 5000001), valueDate: endOfJan))
    }

    func testDoubleEntryBooking() {
        let accounting = FinancialAccounting()
        accounting.baseCurrency = Currency.getInstance("CHF")
        let jan1 = YearMonth.of(2020, 1).atDay(1)
        let endOfJan = YearMonth.of(2020, 1).atEndOfMonth()
        // Credit line: Bank + / Debt -
        accounting.book("Credit", bookingDate: jan1, valueDate: jan1,
                        DebitCreditAmount(debit: FinancialAccounting.BANK, credit: FinancialAccounting.LONG_TERM_DEBT, amount: Money.of("CHF", 10000000)))
        // Buy real estate: Real Estate + / Bank -
        accounting.book("Buy Building", bookingDate: jan1, valueDate: jan1,
                        DebitCreditAmount(debit: FinancialAccounting.REAL_ESTATE, credit: FinancialAccounting.BANK, amount: Money.of("CHF", 2000000)))
        XCTAssertEqual(Money.of("CHF", 8000000), accounting.getBankBalance(endOfJan))
        XCTAssertEqual(Money.of("CHF", 2000000), accounting.getRealEstateBalance(endOfJan))
        XCTAssertEqual(Money.of("CHF", -10000000), accounting.getLongTermDebt(endOfJan))
        // Owners capital = sum of balance sheet = 8M + 2M - 10M = 0
        XCTAssertEqual(Money.of("CHF", 0), accounting.getOwnersCapital(endOfJan))
    }

    func testRevenueAndPnL() {
        let accounting = FinancialAccounting()
        accounting.baseCurrency = Currency.getInstance("CHF")
        let jan1 = YearMonth.of(2020, 1).atDay(1)
        let endOfJan = YearMonth.of(2020, 1).atEndOfMonth()
        // Revenue from sales: Bank + / Revenue -
        accounting.book("Sales", bookingDate: jan1, valueDate: jan1,
                        DebitCreditAmount(debit: FinancialAccounting.BANK, credit: FinancialAccounting.PRODUCT_REVENUES, amount: Money.of("CHF", 500000)))
        // Salary expense: Salary + / Bank -
        accounting.book("Salaries", bookingDate: jan1, valueDate: jan1,
                        DebitCreditAmount(debit: FinancialAccounting.SALARIES, credit: FinancialAccounting.BANK, amount: Money.of("CHF", 200000)))
        XCTAssertEqual(Money.of("CHF", -500000), accounting.getRevenues(endOfJan))
        XCTAssertEqual(Money.of("CHF", 200000), accounting.getSalaries(endOfJan))
        // PnL = sum of income statement = -500000 + 200000 = -300000 (negative = profit)
        XCTAssertEqual(Money.of("CHF", -300000), accounting.getPnL(endOfJan))
    }
}
