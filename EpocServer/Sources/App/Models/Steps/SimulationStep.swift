import Foundation
import Score

/// A single month step in the simulation.
/// Equivalent to `com.jore.epoc.bo.step.SimulationStep`.
public final class SimulationStep {
    public var id: Int64?
    public weak var simulation: Simulation!
    public var companySimulationSteps: [CompanySimulationStep] = []
    public var simulationMonth: YearMonth = .of(2020, 1)
    public var isOpen: Bool = false

    public func addCompanySimulationStep(_ companySimulationStep: CompanySimulationStep) {
        companySimulationStep.simulationStep = self
        companySimulationSteps.append(companySimulationStep)
    }

    public func areAllCompanyStepsFinished() -> Bool {
        !companySimulationSteps.contains { $0.isOpen }
    }

    public func getCompanySimulationStep(for company: Company) -> CompanySimulationStep {
        companySimulationSteps.first { $0.company === company }!
    }
}
