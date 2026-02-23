import Fluent
import Vapor

final class CompanySimulationStepModel: Model, Content, @unchecked Sendable {
    static let schema = "company_simulation_steps"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Parent(key: "simulation_step_id") var simulationStep: SimulationStepModel
    @Parent(key: "company_id") var company: CompanyModel
    @Field(key: "is_open") var isOpen: Bool

    @Children(for: \.$companySimulationStep) var distributionSteps: [DistributionStepModel]

    init() {}
}
