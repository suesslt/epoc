import Foundation

/// Container for all simulation configuration settings.
/// Equivalent to `com.jore.epoc.bo.settings.EpocSettings`.
public final class EpocSettings {
    public static let FACTORY_FIXED_COST = "SET0001"
    public static let FACTORY_COST_PER_PRODUCTION_LINE = "SET0002"
    public static let STORAGE_FIXED_COST = "SET0003"
    public static let STORAGE_VARIABLE_COST_PER_SLOT = "SET0004"
    public static let DAILY_CAPACITY_PER_PRODUCTION_LINE = "SET0005"
    public static let FACTORY_CONSTRUCTION_MONTHS = "SET0006"
    public static let STORAGE_CONSTRUCTION_MONTHS = "SET0007"
    public static let PASSWORD_LENGTH = "SET0008"
    public static let DEFAULT_SIMULATION_START_MONTH = "SET0009"
    public static let SIMULATION_TYPE = "SET0010"
    public static let LABOR_COST_PER_PRODUCTION_LINE = "SET0011"
    public static let MAINTENANCE_COST_PER_BUILDING = "SET0012"
    public static let DEBT_INTEREST_RATE = "SET0013"
    public static let RAW_MATERIAL_PURCHASE_PRICE = "SET0014"
    public static let DEMAND_HIGHER_PERCENT = "SET0015"
    public static let DEMAND_HIGHER_PRICE = "SET0016"
    public static let DEMAND_LOWER_PERCENT = "SET0017"
    public static let DEMAND_LOWER_PRICE = "SET0018"
    public static let PRODUCT_LIFECYCLE_DURATION = "SET0019"
    public static let MARKET_ENTRY_COST = "SET0020"
    public static let BASE_CURRENCY = "SET0022"
    public static let DEPRECIATION_RATE = "SET0023"
    public static let INVENTORY_MANAGEMENT_COST = "SET0024"
    public static let HEADQUARTER_COST = "SET0025"
    public static let PRODUCTION_COST = "SET0026"
    public static let PASSIVE_STEPS = "SET0027"
    public static let PRICE_PER_POINT_QUALITY = "SET0028"
    public static let PRICE_PER_MARKETING_CAMPAIGN = "SET0029"
    public static let PRICE_PER_PRODUCTIVITY_POINT = "SET0030"
    public static let FACTOR_DISCOUNT_RATE = "SET0031"

    public var id: Int64?
    public var settings: [String: EpocSetting] = [:]
    public var isTemplate: Bool = false

    public func addSetting(_ setting: EpocSetting) {
        setting.settings = self
        settings[setting.settingKey] = setting
    }

    public func copy() -> EpocSettings {
        let result = EpocSettings()
        result.isTemplate = isTemplate
        for setting in settings.values {
            result.addSetting(setting.copy())
        }
        return result
    }

    // MARK: - Typed Accessors

    public func getBaseCurrency() -> Currency {
        settings[EpocSettings.BASE_CURRENCY]!.asCurrency()
    }

    public func getDailyCapacityPerProductionLine() -> Int {
        settings[EpocSettings.DAILY_CAPACITY_PER_PRODUCTION_LINE]!.asInteger()
    }

    public func getDebtInterestRate() -> Percent {
        settings[EpocSettings.DEBT_INTEREST_RATE]!.asPercent()
    }

    public func getDemandHigherPercent() -> Percent {
        settings[EpocSettings.DEMAND_HIGHER_PERCENT]!.asPercent()
    }

    public func getDemandHigherPrice() -> Money {
        settings[EpocSettings.DEMAND_HIGHER_PRICE]!.asMoney()
    }

    public func getDemandLowerPercent() -> Percent {
        settings[EpocSettings.DEMAND_LOWER_PERCENT]!.asPercent()
    }

    public func getDemandLowerPrice() -> Money {
        settings[EpocSettings.DEMAND_LOWER_PRICE]!.asMoney()
    }

    public func getDepreciationRate() -> Percent {
        settings[EpocSettings.DEPRECIATION_RATE]!.asPercent()
    }

    public func getFactorDiscountRate() -> Percent {
        settings[EpocSettings.FACTOR_DISCOUNT_RATE]!.asPercent()
    }

    public func getFactoryConstructionCost() -> Money {
        settings[EpocSettings.FACTORY_FIXED_COST]!.asMoney()
    }

    public func getFactoryConstructionCostsPerLine() -> Money {
        settings[EpocSettings.FACTORY_COST_PER_PRODUCTION_LINE]!.asMoney()
    }

    public func getHeadquarterCost() -> Money {
        settings[EpocSettings.HEADQUARTER_COST]!.asMoney()
    }

    public func getInventoryManagementCost() -> Money {
        settings[EpocSettings.INVENTORY_MANAGEMENT_COST]!.asMoney()
    }

    public func getMaintenanceCostPerBuilding() -> Money {
        settings[EpocSettings.MAINTENANCE_COST_PER_BUILDING]!.asMoney()
    }

    public func getPassiveSteps() -> Int {
        settings[EpocSettings.PASSIVE_STEPS]!.asInteger()
    }

    public func getPasswordLength() -> Int {
        settings[EpocSettings.PASSWORD_LENGTH]!.asInteger()
    }

    public func getPricePerMarketingCampaign() -> Money {
        settings[EpocSettings.PRICE_PER_MARKETING_CAMPAIGN]!.asMoney()
    }

    public func getPricePerPercentPointProductivity() -> Money {
        settings[EpocSettings.PRICE_PER_PRODUCTIVITY_POINT]!.asMoney()
    }

    public func getPricePerPercentPointQuality() -> Money {
        settings[EpocSettings.PRICE_PER_POINT_QUALITY]!.asMoney()
    }

    public func getProductionCostPerProduct() -> Money {
        settings[EpocSettings.PRODUCTION_COST]!.asMoney()
    }

    public func getProductionLineLaborCost() -> Money {
        settings[EpocSettings.LABOR_COST_PER_PRODUCTION_LINE]!.asMoney()
    }

    public func getProductLifecycleDuration() -> Int {
        settings[EpocSettings.PRODUCT_LIFECYCLE_DURATION]!.asInteger()
    }

    public func getRawMaterialUnitPrice() -> Money {
        settings[EpocSettings.RAW_MATERIAL_PURCHASE_PRICE]!.asMoney()
    }

    public func getSettingByKey(_ settingKey: String) -> EpocSetting? {
        settings[settingKey]
    }

    public func getSimulationStartMonth() -> YearMonth {
        settings[EpocSettings.DEFAULT_SIMULATION_START_MONTH]!.asYearMonth()
    }

    public func getSimulationType() -> SimulationType {
        SimulationType(rawValue: settings[EpocSettings.SIMULATION_TYPE]!.asString())!
    }

    public func getStorageConstructionMonths() -> Int {
        settings[EpocSettings.STORAGE_CONSTRUCTION_MONTHS]!.asInteger()
    }

    public func getStorageCostPerUnit() -> Money {
        settings[EpocSettings.STORAGE_VARIABLE_COST_PER_SLOT]!.asMoney()
    }

    public func getStorageFixedCost() -> Money {
        settings[EpocSettings.STORAGE_FIXED_COST]!.asMoney()
    }

    public func getTimeToBuild() -> Int {
        settings[EpocSettings.FACTORY_CONSTRUCTION_MONTHS]!.asInteger()
    }
}
