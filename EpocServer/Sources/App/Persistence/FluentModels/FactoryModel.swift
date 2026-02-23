import Fluent
import Vapor

final class FactoryModel: Model, Content, @unchecked Sendable {
    static let schema = "factories"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Parent(key: "company_id") var company: CompanyModel
    @Field(key: "production_lines") var productionLines: Int
    @Field(key: "production_start_month") var productionStartMonth: String
    @Field(key: "daily_capacity_per_production_line") var dailyCapacityPerProductionLine: Int
    @Field(key: "labour_cost_amount") var labourCostAmount: Double
    @Field(key: "labour_cost_currency") var labourCostCurrency: String

    init() {}
}
