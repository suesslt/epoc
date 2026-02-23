/// Severity level for simulation messages. Equivalent to `com.jore.epoc.bo.message.MessageLevel`.
public enum MessageLevel: String, Codable, Sendable {
    case warning = "WARNING"
    case information = "INFORMATION"
}
