import Vapor

public struct SimulationDTO: Content {
    public var id: Int64?
    public var name: String?
    public var startMonth: String?
    public var nrOfMonths: Int?
    public var companies: [CompanyDTO]?
    public var settings: [SettingDTO]?
}

public struct CompanyDTO: Content {
    public var id: Int64?
    public var simulationId: Int64?
    public var name: String?
    public var users: [UserDTO]?
}

public struct UserDTO: Content {
    public var id: Int64?
    public var email: String?
    public var firstName: String?
    public var lastName: String?
    public var username: String?
    public var phone: String?
}

public struct SettingDTO: Content {
    public var settingKey: String
    public var valueText: String
}

public struct SimulationStatisticsDTO: Content {
    public var totalSoldProducts: Int?
}
