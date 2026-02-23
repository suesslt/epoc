import Fluent
import Vapor

final class MarketModel: Model, Content, @unchecked Sendable {
    static let schema = "markets"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Field(key: "name") var name: String
    @Field(key: "gdp_amount") var gdpAmount: Double?
    @Field(key: "gdp_currency") var gdpCurrency: String?
    @Field(key: "gdp_ppp_amount") var gdpPppAmount: Double?
    @Field(key: "gdp_ppp_currency") var gdpPppCurrency: String?
    @Field(key: "gdp_growth") var gdpGrowth: Double?
    @Field(key: "unemployment") var unemployment: Double?
    @Field(key: "life_expectancy") var lifeExpectancy: Double?
    @Field(key: "labor_force") var laborForce: Int?
    @Field(key: "cost_to_enter_market_amount") var costToEnterMarketAmount: Double?
    @Field(key: "cost_to_enter_market_currency") var costToEnterMarketCurrency: String?
    @Field(key: "distribution_cost_amount") var distributionCostAmount: Double?
    @Field(key: "distribution_cost_currency") var distributionCostCurrency: String?
    @Field(key: "age_to14_male") var ageTo14Male: Int
    @Field(key: "age_to14_female") var ageTo14Female: Int
    @Field(key: "age_to24_male") var ageTo24Male: Int
    @Field(key: "age_to24_female") var ageTo24Female: Int
    @Field(key: "age_to54_male") var ageTo54Male: Int
    @Field(key: "age_to54_female") var ageTo54Female: Int
    @Field(key: "age_to64_male") var ageTo64Male: Int
    @Field(key: "age_to64_female") var ageTo64Female: Int
    @Field(key: "age65older_male") var age65olderMale: Int
    @Field(key: "age65older_female") var age65olderFemale: Int

    init() {}
}
