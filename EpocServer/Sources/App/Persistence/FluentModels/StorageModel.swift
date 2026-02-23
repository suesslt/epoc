import Fluent
import Vapor

final class StorageModel: Model, Content, @unchecked Sendable {
    static let schema = "storages"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Parent(key: "company_id") var company: CompanyModel
    @Field(key: "capacity") var capacity: Int
    @Field(key: "storage_start_month") var storageStartMonth: String
    @Field(key: "stored_products") var storedProducts: Int
    @Field(key: "stored_raw_materials") var storedRawMaterials: Int
    @Field(key: "inventory_management_cost_amount") var inventoryManagementCostAmount: Double
    @Field(key: "inventory_management_cost_currency") var inventoryManagementCostCurrency: String

    init() {}
}
