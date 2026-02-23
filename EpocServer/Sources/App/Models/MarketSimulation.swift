import Foundation

/// Links a market to a simulation with demand curve configuration.
/// Equivalent to `com.jore.epoc.bo.MarketSimulation`.
public final class MarketSimulation {
    public var id: Int64?
    public var market: Market = Market()
    public weak var simulation: Simulation!
    public var distributionInMarkets: [DistributionInMarket] = []
    public var startMonth: YearMonth = .of(2020, 1)
    public var higherPrice: Money = Money.of("CHF", 0)
    public var higherPercent: Percent = .zero
    public var lowerPrice: Money = Money.of("CHF", 0)
    public var lowerPercent: Percent = .zero
    public var productLifecycleDuration: Int = 0

    public func addDistributionInMarket(_ distributionInMarket: DistributionInMarket) {
        distributionInMarket.marketSimulation = self
        distributionInMarkets.append(distributionInMarket)
    }

    public func calculateMarketPotentialForProductPrice(marketSize: Int, offeredPrice: Money, qualityFactor: Double) -> Int {
        let demandCurve = DemandCurve(higherPrice: higherPrice, higherPercent: higherPercent, lowerPrice: lowerPrice, lowerPercent: lowerPercent)
        return demandCurve.getDemandForPrice(offeredPrice.divide(qualityFactor)).applyTo(marketSize)
    }

    public func getSoldProducts() -> Int {
        distributionInMarkets.reduce(0) { $0 + $1.getSoldProducts() }
    }

    public func simulateMarket(_ simulationMonth: YearMonth) {
        let marketSize = market.getMarketSizeForConsumption()
        let productsSold = getSoldProducts()
        let availableMarketSize = marketSize - productsSold
        for distributionInMarket in distributionInMarkets {
            addDistributionStep(distributionInMarket, simulationMonth: simulationMonth)
            let marketPotentialForProduct = calculateMarketPotentialForProductPrice(
                marketSize: marketSize,
                offeredPrice: distributionInMarket.getOfferedPrice(simulationMonth: simulationMonth)!,
                qualityFactor: distributionInMarket.company.qualityFactor
            )
            distributionInMarket.setMarketPotentialForProduct(simulationMonth, marketPotentialForProduct: marketPotentialForProduct)
        }
        let totalMarketPotential = distributionInMarkets.reduce(0) { $0 + $1.getMarketPotentialForProduct(simulationMonth: simulationMonth) }
        for distributionInMarket in distributionInMarkets {
            let marketPotentialForProduct = distributionInMarket.getMarketPotentialForProduct(simulationMonth: simulationMonth)
            let availableMarketPotentialForProduct = Int((Double(marketPotentialForProduct) / Double(totalMarketPotential) * Double(availableMarketSize)).rounded())
            let percentageSold = ProductLifecycle(Int(Double(productLifecycleDuration) / distributionInMarket.company.marketingFactor))
                .getPercentageSoldForMonths(YearMonth.monthDiff(end: simulationMonth, start: startMonth))
            let maximumToSell = Int(Double(availableMarketPotentialForProduct) * percentageSold)
            distributionInMarket.company.sellMaximumOf(distributionInMarket, simulationMonth: simulationMonth, maximumToSell: maximumToSell, offeredPrice: distributionInMarket.getOfferedPrice(simulationMonth: simulationMonth)!)
        }
    }

    private func addDistributionStep(_ distributionInMarket: DistributionInMarket, simulationMonth: YearMonth) {
        let distributionStep = DistributionStep()
        distributionStep.offeredPrice = distributionInMarket.offeredPrice
        distributionStep.intentedProductSale = distributionInMarket.intentedProductSale
        let companyStep = distributionInMarket.company.companySimulationSteps.first { step in
            step.simulationStep.simulationMonth == simulationMonth
        }!
        companyStep.addDistributionStep(distributionStep)
        distributionInMarket.addDistributionStep(distributionStep)
    }
}
