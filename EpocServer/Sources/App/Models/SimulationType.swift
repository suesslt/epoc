/// Type of simulation setup. Equivalent to `com.jore.epoc.bo.SimulationType`.
public enum SimulationType: String, Codable, Sendable {
    case inMarket = "IN_MARKET"
    case virgin = "VIRGIN"
}
