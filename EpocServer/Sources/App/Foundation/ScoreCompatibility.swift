import Foundation
import Score

// MARK: - Money Compatibility

/// Bridges epoc's Java-style Money API to score's Swift-style API.
extension Money {
    // MARK: Factory Methods

    static func of(_ currency: Currency, _ amount: Decimal) -> Money {
        Money(amount: amount, currency: currency)
    }

    static func of(_ currencyCode: String, _ amount: Double) -> Money {
        Money(amount: Decimal(amount), currency: Currency(rawValue: currencyCode)!)
    }

    static func of(_ currencyCode: String, _ amount: Decimal) -> Money {
        Money(amount: amount, currency: Currency(rawValue: currencyCode)!)
    }

    static func of(_ currency: Currency, _ amount: Double) -> Money {
        Money(amount: Decimal(amount), currency: currency)
    }

    static func of(_ currency: Currency, _ amount: Int) -> Money {
        Money(amount: Decimal(amount), currency: currency)
    }

    static func of(_ currencyCode: String, _ amount: Int) -> Money {
        Money(amount: Decimal(amount), currency: Currency(rawValue: currencyCode)!)
    }

    // MARK: Parsing

    static func parse(_ string: String) -> Money {
        let trimmed = string.trimmingCharacters(in: .whitespaces)
        // Try "CCC amount" format (e.g. "CHF 1000000")
        if trimmed.count > 3 && trimmed.prefix(3).allSatisfy({ $0.isUppercase }) {
            let currencyCode = String(trimmed.prefix(3))
            let amountString = trimmed.dropFirst(3).trimmingCharacters(in: .whitespaces)
            if let amount = Decimal(string: amountString) {
                return Money(amount: amount, currency: Currency(rawValue: currencyCode)!)
            }
        }
        // Try "amount CCC" format (e.g. "1000 CHF")
        if trimmed.count > 3 && trimmed.suffix(3).allSatisfy({ $0.isUppercase }) {
            let currencyCode = String(trimmed.suffix(3))
            let amountString = trimmed.dropLast(3).trimmingCharacters(in: .whitespaces)
            if let amount = Decimal(string: amountString) {
                return Money(amount: amount, currency: Currency(rawValue: currencyCode)!)
            }
        }
        preconditionFailure("Cannot parse Money from '\(string)'")
    }

    // MARK: Arithmetic Methods

    func add(_ other: Money) -> Money {
        self + other
    }

    func subtract(_ other: Money) -> Money {
        self - other
    }

    func multiply(_ factor: Int) -> Money {
        self * Decimal(factor)
    }

    func multiply(_ factor: Double) -> Money {
        Money(amount: Decimal(amount.doubleValue * factor), currency: currency)
    }

    func multiply(_ factor: Decimal) -> Money {
        self * factor
    }

    func multiply(_ percent: Percent) -> Money {
        self * percent.factorAmount
    }

    func divide(_ divisor: Int) -> Money {
        Money(amount: Decimal(amount.doubleValue / Double(divisor)), currency: currency)
    }

    func divide(_ divisor: Double) -> Money {
        Money(amount: Decimal(amount.doubleValue / divisor), currency: currency)
    }

    func divide(_ divisor: Decimal) -> Money {
        Money(amount: Decimal(amount.doubleValue / divisor.doubleValue), currency: currency)
    }

    /// Divides this Money by another Money of the same currency, returning a Decimal ratio.
    func divide(_ other: Money) -> Decimal {
        precondition(currency == other.currency,
                     "Currency mismatch: \(currency) vs \(other.currency)")
        return Decimal(amount.doubleValue / other.amount.doubleValue)
    }

    func negate() -> Money {
        -self
    }

    func abs() -> Money {
        absoluteValue
    }

    // MARK: Properties

    var signum: Int {
        if amount > 0 { return 1 }
        if amount < 0 { return -1 }
        return 0
    }

    var roundedAmount: Decimal {
        var result = amount
        var rounded = Decimal()
        NSDecimalRound(&rounded, &result, currency.decimalPlaces, .bankers)
        return rounded
    }

    var doubleValue: Double {
        amount.doubleValue
    }
}

extension Money: @retroactive CustomStringConvertible {
    public var description: String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.minimumFractionDigits = currency.decimalPlaces
        formatter.maximumFractionDigits = currency.decimalPlaces
        formatter.groupingSeparator = ""
        formatter.decimalSeparator = "."
        let formatted = formatter.string(from: roundedAmount as NSDecimalNumber) ?? "\(roundedAmount)"
        return "\(currency.rawValue) \(formatted)"
    }
}

// MARK: - Currency Compatibility

/// Bridges epoc's Java-style Currency API to score's enum-based API.
extension Currency {
    static func getInstance(_ currencyCode: String) -> Currency {
        guard let currency = Currency(rawValue: currencyCode) else {
            preconditionFailure("Unknown currency code: \(currencyCode)")
        }
        return currency
    }

    var currencyCode: String { rawValue }
    var fractionDigits: Int { decimalPlaces }

    var roundingUnit: Decimal {
        Decimal(sign: .plus, exponent: -decimalPlaces, significand: 1)
    }
}

// MARK: - Percent Compatibility

extension Percent {
    static let scale = 6

    var toBigDecimal: Decimal { displayValue }
}

// MARK: - Decimal / Double Extensions

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
