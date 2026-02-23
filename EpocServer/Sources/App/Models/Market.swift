import Foundation

/// A geographic market with demographic data.
/// Equivalent to `com.jore.epoc.bo.Market`.
public final class Market {
    public var id: Int64?
    public var name: String = ""
    public var gdpPpp: Money?
    public var gdp: Money?
    public var gdpGrowth: Percent?
    public var unemployment: Percent?
    public var lifeExpectancy: Decimal = 0
    public var laborForce: Int?
    public var costToEnterMarket: Money?
    public var distributionCost: Money?

    // Age distribution fields
    public var ageTo14Male: Int = 0 { didSet { ageTableUpdated = false } }
    public var ageTo14Female: Int = 0 { didSet { ageTableUpdated = false } }
    public var ageTo24Male: Int = 0 { didSet { ageTableUpdated = false } }
    public var ageTo24Female: Int = 0 { didSet { ageTableUpdated = false } }
    public var ageTo54Male: Int = 0 { didSet { ageTableUpdated = false } }
    public var ageTo54Female: Int = 0 { didSet { ageTableUpdated = false } }
    public var ageTo64Male: Int = 0 { didSet { ageTableUpdated = false } }
    public var ageTo64Female: Int = 0 { didSet { ageTableUpdated = false } }
    public var age65olderMale: Int = 0 { didSet { ageTableUpdated = false } }
    public var age65olderFemale: Int = 0 { didSet { ageTableUpdated = false } }

    private var ageTableUpdated = false
    private var ageTableMale: [Int] = []
    private var ageTableFemale: [Int] = []

    // MARK: - Market Size

    public func getMarketSizeForConsumption() -> Int {
        laborForce ?? 0
    }

    public func calculateMarketPotential(startMonth: YearMonth, simulationMonth: YearMonth, productLifecycleDuration: Int) -> Int {
        let productLifecycle = ProductLifecycle(productLifecycleDuration)
        let percentageSold = productLifecycle.getPercentageSoldForMonths(YearMonth.monthDiff(end: simulationMonth, start: startMonth))
        let marketSizeForConsumption = getMarketSizeForConsumption()
        return Int(Double(marketSizeForConsumption) * percentageSold)
    }

    // MARK: - Population

    public func getMalePopulation() -> Int {
        updateAgeTable()
        return ageTableMale.reduce(0, +)
    }

    public func getFemalePopulation() -> Int {
        updateAgeTable()
        return ageTableFemale.reduce(0, +)
    }

    public func getTotalPopulation() -> Int {
        getMalePopulation() + getFemalePopulation()
    }

    public func getPopulationForAge(_ age: Int) -> Int {
        updateAgeTable()
        return age < ageTableMale.count ? ageTableMale[age] + ageTableFemale[age] : 0
    }

    // MARK: - Age Table

    private func updateAgeTable() {
        let lifeExpectancyInt = NSDecimalNumber(decimal: lifeExpectancy).intValue
        precondition(lifeExpectancyInt > 65, "Life Expectancy must be greater than 65")
        guard !ageTableUpdated else { return }
        let maximumAge = lifeExpectancyInt + (lifeExpectancyInt - 65)
        ageTableMale = [Int](repeating: 0, count: maximumAge)
        ageTableFemale = [Int](repeating: 0, count: maximumAge)
        for i in 0..<15 {
            ageTableMale[i] = calculateForYear(ageTo14Male, years: 15)
            ageTableFemale[i] = calculateForYear(ageTo14Female, years: 15)
        }
        for i in 15..<25 {
            ageTableMale[i] = calculateForYear(ageTo24Male, years: 10)
            ageTableFemale[i] = calculateForYear(ageTo24Female, years: 10)
        }
        for i in 25..<55 {
            ageTableMale[i] = calculateForYear(ageTo54Male, years: 30)
            ageTableFemale[i] = calculateForYear(ageTo54Female, years: 30)
        }
        for i in 55..<65 {
            ageTableMale[i] = calculateForYear(ageTo64Male, years: 10)
            ageTableFemale[i] = calculateForYear(ageTo64Female, years: 10)
        }
        var divider = 0
        for i in 0..<(maximumAge - 65) {
            divider += (i + 1)
        }
        for i in 65..<maximumAge {
            ageTableMale[i] = calculateForAgeOver65(age65olderMale, divider: divider, maximumAge: maximumAge, index: i)
            ageTableFemale[i] = calculateForAgeOver65(age65olderFemale, divider: divider, maximumAge: maximumAge, index: i)
        }
        ageTableUpdated = true
    }

    private func calculateForYear(_ number: Int, years: Int) -> Int {
        Int((Float(number) / Float(years)).rounded())
    }

    private func calculateForAgeOver65(_ number: Int, divider: Int, maximumAge: Int, index: Int) -> Int {
        Int((Float(number) / Float(divider) * Float(maximumAge - 65 - (index - 65))).rounded())
    }
}
