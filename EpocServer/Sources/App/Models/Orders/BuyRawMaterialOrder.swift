import Foundation

public final class BuyRawMaterialOrder: SimulationOrder {
    public var id: Int64?
    public var executionMonth: YearMonth = .of(2020, 1)
    public var isExecuted: Bool = false
    public weak var company: Company!
    public var amount: Int = 0
    public var unitPrice: Money = Money.of("CHF", 300)

    public var sortOrder: Int { 3 }
    public var type: String { "Buy raw material" }

    public func getAmount() -> Money {
        unitPrice.multiply(amount)
    }

    public func execute() {
        let storageCapacity = company.storages.reduce(0) { $0 + $1.getAvailableCapacity(executionMonth) }
        if storageCapacity >= amount && company.accounting.checkFunds(getAmount(), valueDate: executionMonth.atEndOfMonth()) {
            Storage.distributeRawMaterialAcrossStorages(company.storages, amount: amount, month: executionMonth, unitPrice: unitPrice)
            book(bookingDate: executionMonth.atDay(1), bookingText: "Buy of raw material",
                 debitAccount1: FinancialAccounting.MATERIALAUFWAND, creditAccount1: FinancialAccounting.BANK, amount1: getAmount(),
                 debitAccount2: FinancialAccounting.RAW_MATERIALS, creditAccount2: FinancialAccounting.BESTANDESAENDERUNGEN_ROHWAREN, amount2: getAmount())
            addMessage(.information, "RawMaterialBought", amount, executionMonth)
            isExecuted = true
        } else {
            if !company.accounting.checkFunds(getAmount(), valueDate: executionMonth.atEndOfMonth()) {
                addMessage(.warning, "NoRawMaterialFunds", executionMonth, getAmount(), company.accounting.getBankBalance(executionMonth.atEndOfMonth()))
            } else {
                addMessage(.warning, "NoRawMaterialCapacity", executionMonth, amount, storageCapacity)
            }
        }
    }
}
