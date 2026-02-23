import Foundation

/// Immutable monetary value with currency. Equivalent to `com.jore.datatypes.money.Money`.
///
/// Uses `Decimal` (Swift's equivalent to Java's `BigDecimal`) for precise financial calculations.
/// All arithmetic operations validate that currencies match.
public struct Money: Hashable, Comparable, Codable, CustomStringConvertible, Sendable {
    public let amount: Decimal
    public let currency: Currency

    private init(currency: Currency, amount: Decimal) {
        self.currency = currency
        self.amount = amount
    }

    // MARK: - Factory Methods

    /// Creates Money from a currency and a Decimal amount.
    public static func of(_ currency: Currency, _ amount: Decimal) -> Money {
        Money(currency: currency, amount: amount)
    }

    /// Creates Money from a currency code string and a Double amount.
    public static func of(_ currencyCode: String, _ amount: Double) -> Money {
        Money(currency: Currency.getInstance(currencyCode), amount: Decimal(amount))
    }

    /// Creates Money from a currency code string and a Decimal amount.
    public static func of(_ currencyCode: String, _ amount: Decimal) -> Money {
        Money(currency: Currency.getInstance(currencyCode), amount: amount)
    }

    /// Creates Money from a Currency and a Double amount.
    public static func of(_ currency: Currency, _ amount: Double) -> Money {
        Money(currency: currency, amount: Decimal(amount))
    }

    /// Creates Money from a Currency and an Int amount.
    public static func of(_ currency: Currency, _ amount: Int) -> Money {
        Money(currency: currency, amount: Decimal(amount))
    }

    /// Creates Money from a currency code and an Int amount.
    public static func of(_ currencyCode: String, _ amount: Int) -> Money {
        Money(currency: Currency.getInstance(currencyCode), amount: Decimal(amount))
    }

    /// Parses a Money string like "CHF 1000000" or "1000 CHF".
    public static func parse(_ string: String) -> Money {
        let trimmed = string.trimmingCharacters(in: .whitespaces)
        // Try "CCC amount" format (e.g. "CHF 1000000")
        if trimmed.count > 3 && trimmed.prefix(3).allSatisfy({ $0.isUppercase }) {
            let currencyCode = String(trimmed.prefix(3))
            let amountString = trimmed.dropFirst(3).trimmingCharacters(in: .whitespaces)
            if let amount = Decimal(string: amountString) {
                return Money(currency: Currency.getInstance(currencyCode), amount: amount)
            }
        }
        // Try "amount CCC" format (e.g. "1000 CHF")
        if trimmed.count > 3 && trimmed.suffix(3).allSatisfy({ $0.isUppercase }) {
            let currencyCode = String(trimmed.suffix(3))
            let amountString = trimmed.dropLast(3).trimmingCharacters(in: .whitespaces)
            if let amount = Decimal(string: amountString) {
                return Money(currency: Currency.getInstance(currencyCode), amount: amount)
            }
        }
        preconditionFailure("Cannot parse Money from '\(string)'")
    }

    // MARK: - Null-safe Static Operations

    /// Adds two Money values, handling nil gracefully (like Java's `Money.add()`).
    public static func add(_ money1: Money?, _ money2: Money?) -> Money? {
        if let m1 = money1, let m2 = money2 {
            return m1.add(m2)
        }
        return money1 ?? money2
    }

    // MARK: - Arithmetic

    public func add(_ other: Money) -> Money {
        assertSameCurrency(other)
        return Money(currency: currency, amount: amount + other.amount)
    }

    public func subtract(_ other: Money) -> Money {
        assertSameCurrency(other)
        return Money(currency: currency, amount: amount - other.amount)
    }

    public func multiply(_ factor: Int) -> Money {
        Money(currency: currency, amount: amount * Decimal(factor))
    }

    public func multiply(_ factor: Double) -> Money {
        Money(currency: currency, amount: Decimal(amount.doubleValue * factor))
    }

    public func multiply(_ factor: Decimal) -> Money {
        Money(currency: currency, amount: amount * factor)
    }

    public func multiply(_ percent: Percent) -> Money {
        Money(currency: currency, amount: amount * percent.factorAmount)
    }

    public func divide(_ divisor: Int) -> Money {
        Money(currency: currency, amount: Decimal(amount.doubleValue / Double(divisor)))
    }

    public func divide(_ divisor: Double) -> Money {
        Money(currency: currency, amount: Decimal(amount.doubleValue / divisor))
    }

    public func divide(_ divisor: Decimal) -> Money {
        Money(currency: currency, amount: Decimal(amount.doubleValue / divisor.doubleValue))
    }

    /// Divides this Money by another Money of the same currency, returning a Decimal ratio.
    public func divide(_ other: Money) -> Decimal {
        assertSameCurrency(other)
        return Decimal(amount.doubleValue / other.amount.doubleValue)
    }

    public func negate() -> Money {
        Money(currency: currency, amount: -amount)
    }

    public func abs() -> Money {
        Money(currency: currency, amount: Swift.abs(amount.doubleValue).asDecimal)
    }

    /// Returns the sign: -1, 0, or 1.
    public var signum: Int {
        if amount > 0 { return 1 }
        if amount < 0 { return -1 }
        return 0
    }

    // MARK: - Comparable

    public static func < (lhs: Money, rhs: Money) -> Bool {
        if lhs.currency == rhs.currency {
            return lhs.roundedAmount < rhs.roundedAmount
        }
        return lhs.currency < rhs.currency
    }

    public static func == (lhs: Money, rhs: Money) -> Bool {
        lhs.currency == rhs.currency && lhs.roundedAmount == rhs.roundedAmount
    }

    // MARK: - Hashable

    public func hash(into hasher: inout Hasher) {
        hasher.combine(currency)
        hasher.combine(roundedAmount)
    }

    // MARK: - Helpers

    /// Amount rounded to the currency's fraction digits (for display and comparison).
    public var roundedAmount: Decimal {
        var result = amount
        var rounded = Decimal()
        NSDecimalRound(&rounded, &result, currency.fractionDigits, .bankers)
        return rounded
    }

    /// The double value of the amount.
    public var doubleValue: Double {
        amount.doubleValue
    }

    private func assertSameCurrency(_ other: Money) {
        precondition(currency == other.currency,
                     "Currency mismatch: \(currency) vs \(other.currency)")
    }

    // MARK: - CustomStringConvertible

    public var description: String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.minimumFractionDigits = currency.fractionDigits
        formatter.maximumFractionDigits = currency.fractionDigits
        formatter.groupingSeparator = ""
        formatter.decimalSeparator = "."
        let formatted = formatter.string(from: roundedAmount as NSDecimalNumber) ?? "\(roundedAmount)"
        return "\(currency.currencyCode) \(formatted)"
    }

    // MARK: - Codable

    private enum CodingKeys: String, CodingKey {
        case amount, currency
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.amount = try container.decode(Decimal.self, forKey: .amount)
        self.currency = try container.decode(Currency.self, forKey: .currency)
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(roundedAmount, forKey: .amount)
        try container.encode(currency, forKey: .currency)
    }
}

// MARK: - Decimal Extension

extension Decimal {
    var doubleValue: Double {
        NSDecimalNumber(decimal: self).doubleValue
    }
}

extension Double {
    var asDecimal: Decimal {
        Decimal(self)
    }
}
