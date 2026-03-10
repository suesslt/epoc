import Foundation
import Score

/// A message generated during simulation execution.
/// Equivalent to `com.jore.epoc.bo.message.Message`.
public final class Message {
    public var id: Int64?
    public weak var company: Company!
    public var relevantMonth: YearMonth = .of(2020, 1)
    public var message: String = ""
    public var level: MessageLevel = .information

    public func setMessage(_ key: String, _ params: Any...) {
        self.message = Messages.getMessage(key, params)
    }
}
