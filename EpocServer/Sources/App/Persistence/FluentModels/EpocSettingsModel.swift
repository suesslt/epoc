import Fluent
import Vapor

final class EpocSettingsModel: Model, Content, @unchecked Sendable {
    static let schema = "epoc_settings"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Field(key: "is_template") var isTemplate: Bool

    @Children(for: \.$settings) var settingEntries: [EpocSettingModel]

    init() {}
}

final class EpocSettingModel: Model, Content, @unchecked Sendable {
    static let schema = "epoc_setting"

    @ID(custom: "id", generatedBy: .database) var id: Int64?
    @Parent(key: "settings_id") var settings: EpocSettingsModel
    @Field(key: "setting_key") var settingKey: String
    @Field(key: "setting_format") var settingFormat: String?
    @Field(key: "value_text") var valueText: String
    @Field(key: "description") var settingDescription: String?

    init() {}
}
