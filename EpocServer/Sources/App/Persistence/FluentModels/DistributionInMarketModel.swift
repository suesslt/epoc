import Fluent
import Vapor

final class DistributionInMarketModel: Model, Content, @unchecked Sendable {
    static let schema = "distribution_in_markets"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Parent(key: "company_id") var company: CompanyModel
    @Parent(key: "market_simulation_id") var marketSimulation: MarketSimulationModel
    @Field(key: "offered_price_amount") var offeredPriceAmount: Double?
    @Field(key: "offered_price_currency") var offeredPriceCurrency: String?
    @Field(key: "intented_product_sale") var intentedProductSale: Int?

    @Children(for: \.$distributionInMarket) var distributionSteps: [DistributionStepModel]

    init() {}
}
