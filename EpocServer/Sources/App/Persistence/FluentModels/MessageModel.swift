import Fluent
import Vapor

final class MessageModel: Model, Content, @unchecked Sendable {
    static let schema = "messages"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Parent(key: "company_id") var company: CompanyModel
    @Field(key: "relevant_month") var relevantMonth: String
    @Field(key: "message") var message: String
    @Field(key: "level") var level: String

    init() {}
}
