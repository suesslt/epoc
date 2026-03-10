import Foundation
import Score

/// A single configuration setting with key, format, value and description.
/// Equivalent to `com.jore.epoc.bo.settings.EpocSetting`.
public final class EpocSetting {
    public var id: Int64?
    public var settingKey: String = ""
    public var settingFormat: String = ""
    public var valueText: String = ""
    public var settingDescription: String = ""
    public weak var settings: EpocSettings!

    public func asCurrency() -> Currency {
        Currency.getInstance(valueText)
    }

    public func asInteger() -> Int {
        Int(valueText)!
    }

    public func asMoney() -> Money {
        Money.parse(valueText)
    }

    public func asPercent() -> Percent {
        Percent.of(valueText)
    }

    public func asString() -> String {
        valueText
    }

    public func asYearMonth() -> YearMonth {
        YearMonth.parse(valueText)
    }

    public func copy() -> EpocSetting {
        let result = EpocSetting()
        result.settingKey = settingKey
        result.settingFormat = settingFormat
        result.valueText = valueText
        result.settingDescription = settingDescription
        return result
    }
}
