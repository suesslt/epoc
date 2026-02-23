import Fluent
import Vapor

final class SimulationStepModel: Model, Content, @unchecked Sendable {
    static let schema = "simulation_steps"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Parent(key: "simulation_id") var simulation: SimulationModel
    @Field(key: "simulation_month") var simulationMonth: String
    @Field(key: "is_open") var isOpen: Bool

    @Children(for: \.$simulationStep) var companySimulationSteps: [CompanySimulationStepModel]

    init() {}
}
