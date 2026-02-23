import Fluent
import Vapor

final class DistributionStepModel: Model, Content, @unchecked Sendable {
    static let schema = "distribution_steps"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Parent(key: "distribution_in_market_id") var distributionInMarket: DistributionInMarketModel
    @Parent(key: "company_simulation_step_id") var companySimulationStep: CompanySimulationStepModel
    @Field(key: "sold_products") var soldProducts: Int
    @Field(key: "intented_product_sale") var intentedProductSale: Int
    @Field(key: "offered_price_amount") var offeredPriceAmount: Double?
    @Field(key: "offered_price_currency") var offeredPriceCurrency: String?
    @Field(key: "market_potential_for_product") var marketPotentialForProduct: Int

    init() {}
}
