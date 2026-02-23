import Vapor

public struct CompanySimulationStepDTO: Content {
    public var id: Int64?
    public var companyName: String?
    public var simulationMonth: String?
    public var companyValue: String?
    public var factories: [FactoryDTO]
    public var storages: [StorageDTO]
    public var distributionInMarkets: [DistributionInMarketDTO]
    public var markets: [MarketDTO]
    public var messages: [MessageDTO]
    public var orders: [CompanyOrderDTO]

    public init() {
        factories = []
        storages = []
        distributionInMarkets = []
        markets = []
        messages = []
        orders = []
    }
}

public struct FactoryDTO: Content {
    public var id: Int64?
    public var capacity: Int?
}

public struct StorageDTO: Content {
    public var id: Int64?
    public var capacity: Int?
}

public struct DistributionInMarketDTO: Content {
    public var id: Int64?
}

public struct MarketDTO: Content {
    public var id: Int64?
    public var name: String?
    public var laborForce: Int?
    public var costToEnterMarket: String?
    public var distributionCost: String?
}

public struct MessageDTO: Content {
    public var level: String?
    public var message: String?
    public var relevantMonth: String?
}

public struct CompanyOrderDTO: Content {
    public var orderType: String?
    public var amount: String?
}
