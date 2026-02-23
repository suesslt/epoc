import Fluent
import Vapor

final class CompanyModel: Model, Content, @unchecked Sendable {
    static let schema = "companies"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Field(key: "name") var name: String
    @Parent(key: "simulation_id") var simulation: SimulationModel
    @Field(key: "quality_factor") var qualityFactor: Double
    @Field(key: "marketing_factor") var marketingFactor: Double
    @Field(key: "productivity_factor") var productivityFactor: Double

    @Children(for: \.$company) var factories: [FactoryModel]
    @Children(for: \.$company) var storages: [StorageModel]
    @Children(for: \.$company) var distributionInMarkets: [DistributionInMarketModel]
    @Children(for: \.$company) var companySimulationSteps: [CompanySimulationStepModel]
    @Children(for: \.$company) var simulationOrders: [SimulationOrderModel]
    @Children(for: \.$company) var messages: [MessageModel]

    init() {}
}
