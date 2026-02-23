import Fluent
import Vapor

final class SimulationModel: Model, Content, @unchecked Sendable {
    static let schema = "simulations"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Field(key: "name") var name: String
    @Field(key: "start_month") var startMonth: String
    @Field(key: "nr_of_months") var nrOfMonths: Int
    @Field(key: "is_started") var isStarted: Bool
    @Field(key: "is_finished") var isFinished: Bool
    @Field(key: "interest_rate") var interestRate: Double
    @Field(key: "maintenance_amount") var buildingMaintenanceAmount: Double
    @Field(key: "maintenance_currency") var buildingMaintenanceCurrency: String
    @Field(key: "depreciation_rate") var depreciationRate: Double
    @Field(key: "headquarter_amount") var headquarterAmount: Double
    @Field(key: "headquarter_currency") var headquarterCurrency: String
    @Field(key: "production_cost_amount") var productionCostAmount: Double
    @Field(key: "production_cost_currency") var productionCostCurrency: String
    @OptionalParent(key: "settings_id") var settings: EpocSettingsModel?

    @Children(for: \.$simulation) var companies: [CompanyModel]
    @Children(for: \.$simulation) var simulationSteps: [SimulationStepModel]
    @Children(for: \.$simulation) var marketSimulations: [MarketSimulationModel]

    init() {}
}
