import Foundation

/// The top-level simulation entity that orchestrates the game.
/// Equivalent to `com.jore.epoc.bo.Simulation`.
public final class Simulation {
    public var id: Int64?
    public var name: String = ""
    public var startMonth: YearMonth?
    public var nrOfMonths: Int?
    public var isStarted: Bool = false
    public var isFinished: Bool = false
    public var interestRate: Percent = .zero
    public var settings: EpocSettings?
    public var companies: [Company] = []
    public var simulationSteps: [SimulationStep] = []
    public var marketSimulations: [MarketSimulation] = []
    public var buildingMaintenanceCost: Money = Money.of("CHF", 0)
    public var depreciationRate: Percent = .zero
    public var headquarterCost: Money = Money.of("CHF", 0)
    public var productionCost: Money = Money.of("CHF", 0)

    // MARK: - Add Methods

    public func addCompany(_ company: Company) {
        company.simulation = self
        companies.append(company)
    }

    public func addMarketSimulation(_ marketSimulation: MarketSimulation) {
        marketSimulation.simulation = self
        marketSimulations.append(marketSimulation)
    }

    public func addSimulationStep(_ simulationStep: SimulationStep) {
        simulationStep.simulation = self
        simulationSteps.append(simulationStep)
    }

    // MARK: - Accessors

    public func getBuildingMaintenanceCost() -> Money {
        buildingMaintenanceCost
    }

    public func getDepreciationRate() -> Percent {
        depreciationRate
    }

    public func getHeadquarterCost() -> Money {
        headquarterCost
    }

    public func getInterestRate() -> Percent {
        interestRate
    }

    public func getProductionCost() -> Money {
        productionCost
    }

    public func getSoldProducts() -> Int {
        marketSimulations.reduce(0) { $0 + $1.getSoldProducts() }
    }

    // MARK: - Simulation Step Management

    public func getActiveSimulationStep() -> SimulationStep? {
        let latestStep = simulationSteps.sorted { $0.simulationMonth > $1.simulationMonth }.first
        if let step = latestStep {
            if step.isOpen {
                return step
            } else {
                let endMonth = startMonth!.plusMonths(nrOfMonths! - 1)
                if step.simulationMonth.isBefore(endMonth) {
                    return createSimulationStep(step.simulationMonth.plusMonths(1))
                } else {
                    isFinished = true
                    return nil
                }
            }
        } else {
            isStarted = true
            return createSimulationStep(startMonth!)
        }
    }

    public func finishCompanyStep(_ companySimulationStep: CompanySimulationStep) {
        companySimulationStep.isOpen = false
        if companySimulationStep.simulationStep.areAllCompanyStepsFinished() {
            simulate(companySimulationStep.simulationStep)
            simulatePassiveSteps()
        }
    }

    // MARK: - Simulation Logic

    private func simulate(_ simulationStep: SimulationStep) {
        for companySimulationStep in simulationStep.companySimulationSteps {
            let company = companySimulationStep.company!
            for simulationOrder in company.getOrdersForExecutionIn(simulationStep.simulationMonth) {
                simulationOrder.execute()
            }
            company.manufactureProducts(simulationStep.simulationMonth)
            company.chargeWorkforceCost(simulationStep.simulationMonth)
            company.chargeInterest(simulationStep.simulationMonth)
            company.chargeDepreciation(simulationStep.simulationMonth)
            company.chargeBuildingMaintenanceCost(simulationStep.simulationMonth)
        }
        for marketSimulation in marketSimulations {
            marketSimulation.simulateMarket(simulationStep.simulationMonth)
        }
        for companySimulationStep in simulationStep.companySimulationSteps {
            companySimulationStep.company.discountFactors()
        }
        simulationStep.isOpen = false
        setSimulationToFinishedIfThisWasTheLastStep(simulationStep)
    }

    private func simulatePassiveSteps() {
        var activeSimulationStep = getActiveSimulationStep()
        let passiveSteps = settings?.getPassiveSteps() ?? 1
        for _ in 0..<(passiveSteps - 1) {
            guard let step = activeSimulationStep else { break }
            for companySimulationStep in step.companySimulationSteps {
                companySimulationStep.isOpen = false
                if companySimulationStep.simulationStep.areAllCompanyStepsFinished() {
                    simulate(companySimulationStep.simulationStep)
                }
            }
            activeSimulationStep = getActiveSimulationStep()
        }
    }

    private func createSimulationStep(_ month: YearMonth) -> SimulationStep {
        let step = SimulationStep()
        step.simulationMonth = month
        step.isOpen = true
        addSimulationStep(step)
        for company in companies {
            let companySimulationStep = CompanySimulationStep()
            companySimulationStep.isOpen = true
            company.addCompanySimulationStep(companySimulationStep)
            step.addCompanySimulationStep(companySimulationStep)
        }
        return step
    }

    private func setSimulationToFinishedIfThisWasTheLastStep(_ simulationStep: SimulationStep) {
        let endMonth = startMonth!.plusMonths(nrOfMonths! - 1)
        if !simulationStep.simulationMonth.isBefore(endMonth) {
            isFinished = true
        }
    }
}
