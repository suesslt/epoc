import Foundation

/// A company's presence and distribution in a specific market.
/// Equivalent to `com.jore.epoc.bo.DistributionInMarket`.
public final class DistributionInMarket {
    public var id: Int64?
    public weak var company: Company!
    public weak var marketSimulation: MarketSimulation!
    public var distributionSteps: [DistributionStep] = []
    public var offeredPrice: Money?
    public var intentedProductSale: Int?

    public func addDistributionStep(_ distributionStep: DistributionStep) {
        distributionStep.distributionInMarket = self
        distributionSteps.append(distributionStep)
    }

    public func getDistributionCost() -> Money? {
        marketSimulation.market.distributionCost
    }

    public func getIntentedProductSale(simulationMonth: YearMonth) -> Int? {
        getDistributionStep(simulationMonth).intentedProductSale
    }

    public func getMarketPotentialForProduct(simulationMonth: YearMonth) -> Int {
        getDistributionStep(simulationMonth).marketPotentialForProduct
    }

    public func getOfferedPrice(simulationMonth: YearMonth) -> Money? {
        getDistributionStep(simulationMonth).offeredPrice
    }

    public func getSoldProducts() -> Int {
        distributionSteps.reduce(0) { $0 + $1.soldProducts }
    }

    public func setMarketPotentialForProduct(_ simulationMonth: YearMonth, marketPotentialForProduct: Int) {
        getDistributionStep(simulationMonth).marketPotentialForProduct = marketPotentialForProduct
    }

    public func setSoldProducts(_ simulationMonth: YearMonth, soldProducts: Int) {
        getDistributionStep(simulationMonth).soldProducts = soldProducts
    }

    private func getDistributionStep(_ simulationMonth: YearMonth) -> DistributionStep {
        distributionSteps.first { step in
            step.companySimulationStep.simulationStep.simulationMonth == simulationMonth
        }!
    }
}
