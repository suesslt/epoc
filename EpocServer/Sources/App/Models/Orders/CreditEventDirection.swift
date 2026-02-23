/// Direction of a credit line adjustment. Equivalent to `com.jore.epoc.bo.orders.CreditEventDirection`.
public enum CreditEventDirection: String, Codable, Sendable {
    case increase = "INCREASE"
    case decrease = "DECREASE"
}
