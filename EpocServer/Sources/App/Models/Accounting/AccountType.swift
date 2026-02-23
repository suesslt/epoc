/// Type of accounting account. Equivalent to `com.jore.epoc.bo.accounting.AccountType`.
public enum AccountType: String, Codable, Sendable {
    case balanceSheet = "BALANCE_SHEET"
    case incomeStatement = "INCOME_STATEMENT"
}
