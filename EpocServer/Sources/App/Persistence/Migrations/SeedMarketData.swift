import Fluent

struct SeedMarketData: AsyncMigration {
    func prepare(on database: Database) async throws {
        // Switzerland - from sql/schema.sql seed data
        let switzerland = MarketModel()
        switzerland.name = "Switzerland"
        switzerland.laborForce = 5236000
        switzerland.lifeExpectancy = 83.0
        switzerland.gdpAmount = 703082000000.0
        switzerland.gdpCurrency = "CHF"
        switzerland.gdpPppAmount = 523400000000.0
        switzerland.gdpPppCurrency = "CHF"
        switzerland.gdpGrowth = 0.018
        switzerland.unemployment = 0.032
        switzerland.costToEnterMarketAmount = 400000.0
        switzerland.costToEnterMarketCurrency = "CHF"
        switzerland.distributionCostAmount = 2000000.0
        switzerland.distributionCostCurrency = "CHF"
        switzerland.ageTo14Male = 622308
        switzerland.ageTo14Female = 589652
        switzerland.ageTo24Male = 437832
        switzerland.ageTo24Female = 414464
        switzerland.ageTo54Male = 1893546
        switzerland.ageTo54Female = 1871070
        switzerland.ageTo64Male = 585924
        switzerland.ageTo64Female = 595512
        switzerland.age65olderMale = 728994
        switzerland.age65olderFemale = 893710
        try await switzerland.save(on: database)
    }

    func revert(on database: Database) async throws {
        try await MarketModel.query(on: database).filter(\.$name == "Switzerland").delete()
    }
}
