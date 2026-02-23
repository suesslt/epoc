import Vapor

public struct AdjustCreditLineDTO: Content, Validatable {
    public var companyId: Int64
    public var executionMonth: String
    public var amount: String

    public static func validations(_ validations: inout Validations) {
        validations.add("companyId", as: Int64.self)
        validations.add("executionMonth", as: String.self, is: !.empty)
        validations.add("amount", as: String.self, is: !.empty)
    }
}

public struct BuildFactoryDTO: Content, Validatable {
    public var companyId: Int64
    public var executionMonth: String
    public var productionLines: Int

    public static func validations(_ validations: inout Validations) {
        validations.add("companyId", as: Int64.self)
        validations.add("productionLines", as: Int.self, is: .range(1...10))
    }
}

public struct BuildStorageDTO: Content, Validatable {
    public var companyId: Int64
    public var executionMonth: String
    public var capacity: Int

    public static func validations(_ validations: inout Validations) {
        validations.add("companyId", as: Int64.self)
        validations.add("capacity", as: Int.self, is: .range(1...1000))
    }
}

public struct BuyRawMaterialDTO: Content, Validatable {
    public var companyId: Int64
    public var executionMonth: String
    public var amount: Int

    public static func validations(_ validations: inout Validations) {
        validations.add("companyId", as: Int64.self)
        validations.add("amount", as: Int.self, is: .range(1...1000))
    }
}

public struct EnterMarketDTO: Content, Validatable {
    public var companyId: Int64
    public var marketId: Int64
    public var executionMonth: String
    public var intentedProductSales: Int
    public var offeredPrice: String

    public static func validations(_ validations: inout Validations) {
        validations.add("companyId", as: Int64.self)
        validations.add("marketId", as: Int64.self)
    }
}

public struct IncreaseQualityDTO: Content {
    public var companyId: Int64
    public var executionMonth: String
    public var increaseQualityAmount: String
}

public struct IncreaseProductivityDTO: Content {
    public var companyId: Int64
    public var executionMonth: String
    public var increaseProductivityAmount: String
}

public struct RunMarketingCampaignDTO: Content {
    public var companyId: Int64
    public var executionMonth: String
    public var campaignAmount: String
}

public struct IntendedSalesAndPriceDTO: Content {
    public var companyId: Int64
    public var executionMonth: String
    public var marketId: Int64
    public var intentedSales: Int
    public var price: String
}

public struct OpenUserSimulationDTO: Content {
    public var simulationId: Int64?
    public var simulationName: String?
    public var companyName: String?
    public var companyId: Int64?
}

public struct CompletedUserSimulationDTO: Content {
    public var simulationId: Int64?
    public var simulationName: String?
    public var companyName: String?
    public var companyId: Int64?
}

public struct CompanyUserDTO: Content {
    public var companyId: Int64
    public var email: String
}
