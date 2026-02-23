import Foundation

/// A company's step within a simulation step (one month).
/// Equivalent to `com.jore.epoc.bo.step.CompanySimulationStep`.
public final class CompanySimulationStep {
    public var id: Int64?
    public var simulationStep: SimulationStep = SimulationStep()
    public weak var company: Company!
    public var isOpen: Bool = false
    public var distributionSteps: [DistributionStep] = []

    public func addDistributionStep(_ distributionStep: DistributionStep) {
        distributionStep.companySimulationStep = self
        distributionSteps.append(distributionStep)
    }

    public func finish() {
        simulationStep.simulation.finishCompanyStep(self)
    }
}
