import Fluent
import Vapor

final class SimulationOrderModel: Model, Content, @unchecked Sendable {
    static let schema = "simulation_orders"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Parent(key: "company_id") var company: CompanyModel
    @Field(key: "order_type") var orderType: String
    @Field(key: "execution_month") var executionMonth: String
    @Field(key: "is_executed") var isExecuted: Bool
    // Generic JSON fields to store order-specific data
    @Field(key: "amount_value") var amountValue: Double?
    @Field(key: "amount_currency") var amountCurrency: String?
    @Field(key: "int_param1") var intParam1: Int?
    @Field(key: "int_param2") var intParam2: Int?
    @Field(key: "double_param1") var doubleParam1: Double?
    @Field(key: "string_param1") var stringParam1: String?
    @Field(key: "money_param1_amount") var moneyParam1Amount: Double?
    @Field(key: "money_param1_currency") var moneyParam1Currency: String?
    @Field(key: "money_param2_amount") var moneyParam2Amount: Double?
    @Field(key: "money_param2_currency") var moneyParam2Currency: String?
    @OptionalField(key: "market_simulation_id") var marketSimulationId: Int64?
    @OptionalField(key: "market_id") var marketId: Int64?

    init() {}
}
