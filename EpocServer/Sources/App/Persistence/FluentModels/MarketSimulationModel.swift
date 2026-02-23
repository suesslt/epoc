import Fluent
import Vapor

final class MarketSimulationModel: Model, Content, @unchecked Sendable {
    static let schema = "market_simulations"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Parent(key: "market_id") var market: MarketModel
    @Parent(key: "simulation_id") var simulation: SimulationModel
    @Field(key: "start_month") var startMonth: String
    @Field(key: "higher_price_amount") var higherPriceAmount: Double
    @Field(key: "higher_price_currency") var higherPriceCurrency: String
    @Field(key: "higher_percent") var higherPercent: Double
    @Field(key: "lower_price_amount") var lowerPriceAmount: Double
    @Field(key: "lower_price_currency") var lowerPriceCurrency: String
    @Field(key: "lower_percent") var lowerPercent: Double
    @Field(key: "product_lifecycle_duration") var productLifecycleDuration: Int

    @Children(for: \.$marketSimulation) var distributionInMarkets: [DistributionInMarketModel]

    init() {}
}
