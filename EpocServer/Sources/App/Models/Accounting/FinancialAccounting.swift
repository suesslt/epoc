import Foundation

/// Full double-entry accounting system per company.
/// Equivalent to `com.jore.epoc.bo.accounting.FinancialAccounting`.
///
/// Chart of accounts:
/// - Assets: BANK (1020), RECEIVABLES (1100), RAW_MATERIALS (1210), PRODUCTS (1260), REAL_ESTATE (1600)
/// - Liabilities: LONG_TERM_DEBT (2100)
/// - Revenue: PRODUCT_REVENUES (3000)
/// - Expenses: MATERIALAUFWAND (4000), SERVICES (4400), BESTANDESAENDERUNGEN_ROHWAREN (4501),
///             BESTANDESAENDERUNGEN_PRODUKTE (4502), SALARIES (5000), RAUMAUFWAND (6000),
///             DEPRECIATION (6800), INTEREST (6900)
public final class FinancialAccounting {
    private static let infinityMultiplier = 6

    // Account numbers
    public static let BANK = "1020"
    public static let RECEIVABLES = "1100"
    public static let RAW_MATERIALS = "1210"
    public static let PRODUCTS = "1260"
    public static let REAL_ESTATE = "1600"
    public static let LONG_TERM_DEBT = "2100"
    public static let PRODUCT_REVENUES = "3000"
    public static let MATERIALAUFWAND = "4000"
    public static let SERVICES = "4400"
    public static let BESTANDESAENDERUNGEN_ROHWAREN = "4501"
    public static let BESTANDESAENDERUNGEN_PRODUKTE = "4502"
    public static let SALARIES = "5000"
    public static let RAUMAUFWAND = "6000"
    public static let DEPRECIATION = "6800"
    public static let INTEREST = "6900"

    public var id: Int64?
    public var baseCurrency: Currency = Currency.getInstance("CHF")
    public var accounts: [Account] = []
    public var journalEntries: [JournalEntry] = []

    public init() {
        addAccount(Account(accountType: .balanceSheet, number: Self.BANK, name: "Bank"))
        addAccount(Account(accountType: .balanceSheet, number: Self.RECEIVABLES, name: "Receivables"))
        addAccount(Account(accountType: .balanceSheet, number: Self.LONG_TERM_DEBT, name: "Bankverbindlichkeiten"))
        addAccount(Account(accountType: .balanceSheet, number: Self.REAL_ESTATE, name: "Liegenschaften"))
        addAccount(Account(accountType: .balanceSheet, number: Self.RAW_MATERIALS, name: "Rohmaterialvorrat"))
        addAccount(Account(accountType: .balanceSheet, number: Self.PRODUCTS, name: "Fertigprodukte"))
        addAccount(Account(accountType: .incomeStatement, number: Self.SERVICES, name: "Bezogene Dienstleistungen"))
        addAccount(Account(accountType: .incomeStatement, number: Self.PRODUCT_REVENUES, name: "Produktionsertrag"))
        addAccount(Account(accountType: .incomeStatement, number: Self.INTEREST, name: "Zinsaufwand"))
        addAccount(Account(accountType: .incomeStatement, number: Self.RAUMAUFWAND, name: "Gebaeudeunterhalt"))
        addAccount(Account(accountType: .incomeStatement, number: Self.DEPRECIATION, name: "Abschreibung"))
        addAccount(Account(accountType: .incomeStatement, number: Self.SALARIES, name: "Personalaufwand"))
        addAccount(Account(accountType: .incomeStatement, number: Self.MATERIALAUFWAND, name: "Materialaufwand Produktion"))
        addAccount(Account(accountType: .incomeStatement, number: Self.BESTANDESAENDERUNGEN_ROHWAREN, name: "Bestandesaenderung Rohwaren"))
        addAccount(Account(accountType: .incomeStatement, number: Self.BESTANDESAENDERUNGEN_PRODUKTE, name: "Bestandesaenderung Fertigprodukte"))
    }

    public func addAccount(_ account: Account) {
        account.accounting = self
        accounts.append(account)
    }

    // MARK: - Booking

    public func book(_ bookingText: String, bookingDate: Date, valueDate: Date, _ debitCreditAmounts: DebitCreditAmount...) {
        precondition(!debitCreditAmounts.isEmpty, "At least one debit to credit amount required.")
        let journalEntry = JournalEntry()
        journalEntry.bookingText = bookingText
        journalEntry.bookingDate = bookingDate
        journalEntry.valueDate = valueDate
        for dca in debitCreditAmounts {
            precondition(baseCurrency == dca.amount.currency,
                         "Booked currency (\(dca.amount.currency)) must be equal to base currency (\(baseCurrency)).")
            precondition(getAccount(dca.debitAccountNumber) != nil,
                         "Account for number '\(dca.debitAccountNumber)' not found.")
            precondition(getAccount(dca.creditAccountNumber) != nil,
                         "Account for number '\(dca.creditAccountNumber)' not found.")
            let booking = Booking()
            booking.amount = dca.amount.amount
            getAccount(dca.debitAccountNumber)!.debit(booking)
            getAccount(dca.creditAccountNumber)!.credit(booking)
            journalEntry.addBooking(booking)
        }
        addJournalEntry(journalEntry)
    }

    // MARK: - Queries

    public func checkFunds(_ minimumRequiredAmount: Money, valueDate: Date) -> Bool {
        getBankBalance(valueDate) >= minimumRequiredAmount
    }

    public func getBalanceForAccount(_ accountNumber: String, valueDate: Date) -> Money {
        precondition(getAccount(accountNumber) != nil, "Account for number '\(accountNumber)' not found.")
        return Money.of(baseCurrency, getAccount(accountNumber)!.getBalance(valueDate))
    }

    public func getBankBalance(_ valueDate: Date) -> Money {
        getBalanceForAccount(Self.BANK, valueDate: valueDate)
    }

    public func getCash(_ valueDate: Date) -> Money {
        getBalanceForAccount(Self.BANK, valueDate: valueDate)
    }

    public func getCompanyValue(_ valueDate: Date) -> Money {
        Money.add(getOwnersCapital(valueDate), getPnL(valueDate).multiply(Self.infinityMultiplier))!
    }

    public func getLongTermDebt(_ valueDate: Date) -> Money {
        getBalanceForAccount(Self.LONG_TERM_DEBT, valueDate: valueDate)
    }

    public func getOwnersCapital(_ valueDate: Date) -> Money {
        let sum = accounts
            .filter { $0.accountType == .balanceSheet }
            .reduce(Decimal(0)) { $0 + $1.getBalance(valueDate) }
        return Money.of(baseCurrency, sum)
    }

    public func getPnL(_ valueDate: Date) -> Money {
        let sum = accounts
            .filter { $0.accountType == .incomeStatement }
            .reduce(Decimal(0)) { $0 + $1.getBalance(valueDate) }
        return Money.of(baseCurrency, sum)
    }

    public func getProductBalance(_ valueDate: Date) -> Money {
        getBalanceForAccount(Self.PRODUCTS, valueDate: valueDate)
    }

    public func getRawMaterialBalance(_ valueDate: Date) -> Money {
        getBalanceForAccount(Self.RAW_MATERIALS, valueDate: valueDate)
    }

    public func getRealEstateBalance(_ valueDate: Date) -> Money {
        getBalanceForAccount(Self.REAL_ESTATE, valueDate: valueDate)
    }

    public func getReceivables(_ valueDate: Date) -> Money {
        getBalanceForAccount(Self.RECEIVABLES, valueDate: valueDate)
    }

    public func getRevenues(_ valueDate: Date) -> Money {
        getBalanceForAccount(Self.PRODUCT_REVENUES, valueDate: valueDate)
    }

    public func getSalaries(_ valueDate: Date) -> Money {
        getBalanceForAccount(Self.SALARIES, valueDate: valueDate)
    }

    public func getTotalAssets(_ valueDate: Date) -> Money {
        getTotalCurrentAssets(valueDate).add(getTotalFixedAssets(valueDate))
    }

    public func getTotalCurrentAssets(_ valueDate: Date) -> Money {
        getBankBalance(valueDate)
            .add(getProductBalance(valueDate))
            .add(getRawMaterialBalance(valueDate))
    }

    public func getTotalCurrentLiabilities(_ valueDate: Date) -> Money {
        getLongTermDebt(valueDate)
    }

    public func getTotalFixedAssets(_ valueDate: Date) -> Money {
        getRealEstateBalance(valueDate)
    }

    public func getTotalLiabilities(_ valueDate: Date) -> Money {
        getTotalCurrentLiabilities(valueDate)
    }

    public func getTotalLiabilitiesAndOwnersEquity(_ valueDate: Date) -> Money {
        getTotalLiabilities(valueDate).add(getOwnersCapital(valueDate))
    }

    public func setStartBalanceForAccount(_ accountNumber: String, balance: Money) {
        getAccount(accountNumber)!.startBalance = balance.amount
    }

    // MARK: - Private

    private func getAccount(_ number: String) -> Account? {
        accounts.first { $0.number == number }
    }

    private func addJournalEntry(_ journalEntry: JournalEntry) {
        journalEntry.accounting = self
        journalEntries.append(journalEntry)
    }
}

// MARK: - CustomStringConvertible

extension FinancialAccounting: CustomStringConvertible {
    public var description: String {
        var result = ""
        for account in accounts {
            result += "\n\(account.number): \(account.startBalance) (\(account.name))"
        }
        result += "\n"
        return result
    }
}
