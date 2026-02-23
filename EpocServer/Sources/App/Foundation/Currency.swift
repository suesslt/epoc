import Foundation

/// Represents an ISO 4217 currency. Equivalent to `com.jore.datatypes.currency.Currency`.
/// Uses a static registry of known currencies.
public struct Currency: Hashable, Comparable, Codable, CustomStringConvertible, Sendable {
    public let currencyCode: String
    public let entity: String
    public let fractionDigits: Int
    public let name: String
    public let numericCode: Int

    private init(currencyCode: String, entity: String, fractionDigits: Int, name: String, numericCode: Int) {
        self.currencyCode = currencyCode
        self.entity = entity
        self.fractionDigits = fractionDigits
        self.name = name
        self.numericCode = numericCode
    }

    /// Returns the rounding unit for this currency (e.g. 0.01 for CHF).
    public var roundingUnit: Decimal {
        Decimal(sign: .plus, exponent: -fractionDigits, significand: 1)
    }

    // MARK: - Registry

    private static let currencies: [String: Currency] = {
        var map = [String: Currency]()
        let data: [(String, String, Int, String, Int)] = [
            ("AED", "UNITED ARAB EMIRATES", 2, "UAE Dirham", 784),
            ("AUD", "AUSTRALIA", 2, "Australian Dollar", 36),
            ("BRL", "BRAZIL", 2, "Brazilian Real", 986),
            ("CAD", "CANADA", 2, "Canadian Dollar", 124),
            ("CHF", "SWITZERLAND", 2, "Swiss Franc", 756),
            ("CNY", "CHINA", 2, "Yuan Renminbi", 156),
            ("CZK", "CZECH REPUBLIC", 2, "Czech Koruna", 203),
            ("DKK", "DENMARK", 2, "Danish Krone", 208),
            ("EUR", "EUROPEAN UNION", 2, "Euro", 978),
            ("GBP", "UNITED KINGDOM", 2, "Pound Sterling", 826),
            ("HKD", "HONG KONG", 2, "Hong Kong Dollar", 344),
            ("HUF", "HUNGARY", 2, "Forint", 348),
            ("INR", "INDIA", 2, "Indian Rupee", 356),
            ("JPY", "JAPAN", 0, "Yen", 392),
            ("KRW", "KOREA, REPUBLIC OF", 0, "Won", 410),
            ("MXN", "MEXICO", 2, "Mexican Peso", 484),
            ("NOK", "NORWAY", 2, "Norwegian Krone", 578),
            ("NZD", "NEW ZEALAND", 2, "New Zealand Dollar", 554),
            ("PLN", "POLAND", 2, "Zloty", 985),
            ("RUB", "RUSSIAN FEDERATION", 2, "Russian Ruble", 643),
            ("SEK", "SWEDEN", 2, "Swedish Krona", 752),
            ("SGD", "SINGAPORE", 2, "Singapore Dollar", 702),
            ("THB", "THAILAND", 2, "Baht", 764),
            ("TRY", "TURKEY", 2, "Turkish Lira", 949),
            ("TWD", "TAIWAN", 2, "New Taiwan Dollar", 901),
            ("USD", "UNITED STATES", 2, "US Dollar", 840),
            ("ZAR", "SOUTH AFRICA", 2, "Rand", 710),
        ]
        for (code, entity, digits, name, numeric) in data {
            map[code] = Currency(currencyCode: code, entity: entity, fractionDigits: digits, name: name, numericCode: numeric)
        }
        return map
    }()

    /// Returns the Currency instance for the given ISO 4217 currency code.
    public static func getInstance(_ currencyCode: String) -> Currency {
        guard let currency = currencies[currencyCode] else {
            preconditionFailure("Unknown currency code: \(currencyCode)")
        }
        return currency
    }

    // MARK: - Comparable

    public static func < (lhs: Currency, rhs: Currency) -> Bool {
        lhs.currencyCode < rhs.currencyCode
    }

    // MARK: - Hashable

    public func hash(into hasher: inout Hasher) {
        hasher.combine(currencyCode)
    }

    public static func == (lhs: Currency, rhs: Currency) -> Bool {
        lhs.currencyCode == rhs.currencyCode
    }

    // MARK: - CustomStringConvertible

    public var description: String {
        currencyCode
    }

    // MARK: - Codable

    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let code = try container.decode(String.self)
        self = Currency.getInstance(code)
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(currencyCode)
    }
}
