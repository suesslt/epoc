import Foundation

/// Distribution results for a company in a market for one simulation step.
/// Equivalent to `com.jore.epoc.bo.step.DistributionStep`.
public final class DistributionStep {
    public var id: Int64?
    public weak var distributionInMarket: DistributionInMarket!
    public var companySimulationStep: CompanySimulationStep = CompanySimulationStep()
    public var soldProducts: Int = 0
    public var intentedProductSale: Int? = 0
    public var offeredPrice: Money?
    public var marketPotentialForProduct: Int = 0
}
