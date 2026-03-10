import Foundation
import Score

/// Inventory storage for raw materials and finished products.
/// Equivalent to `com.jore.epoc.bo.Storage`.
public final class Storage {
    public var id: Int64?
    public weak var company: Company!
    public var storageStartMonth: YearMonth = .of(2020, 1)
    public var capacity: Int = 0
    public var storedProducts: Int = 0
    public var storedRawMaterials: Int = 0
    public var inventoryManagementCost: Money = Money.of("CHF", 500000)
    private var averagePrice: Money?

    // MARK: - Instance Methods

    public func getAvailableCapacity(_ storageMonth: YearMonth) -> Int {
        isBuiltAndReady(storageMonth) ? capacity - getTotalStored() : 0
    }

    public func getTotalStored() -> Int {
        storedProducts + storedRawMaterials
    }

    @discardableResult
    public func storeProducts(_ productsToStore: Int, month: YearMonth) -> Int {
        let result = min(productsToStore, getAvailableCapacity(month))
        storedProducts += result
        return result
    }

    @discardableResult
    public func storeRawMaterials(_ rawMaterialToStore: Int, month: YearMonth, unitPrice: Money) -> Int {
        let result = min(rawMaterialToStore, getAvailableCapacity(month))
        storedRawMaterials += result
        return result
    }

    public func removeProducts(_ productsToRemove: Int) -> Int {
        let result = min(storedProducts, productsToRemove)
        storedProducts -= result
        return result
    }

    public func removeRawMaterials(_ rawMaterialToRemove: Int) -> Int {
        let result = min(storedRawMaterials, rawMaterialToRemove)
        storedRawMaterials -= result
        return result
    }

    func getValue() -> Money? {
        averagePrice != nil ? averagePrice!.multiply(storedRawMaterials) : nil
    }

    func setAveragePrice(_ price: Money) {
        self.averagePrice = price
    }

    private func isBuiltAndReady(_ storageMonth: YearMonth) -> Bool {
        !storageMonth.isBefore(storageStartMonth)
    }

    // MARK: - Static Distribution Methods

    public static func distributeProductAcrossStorages(_ storages: [Storage], productsToStore: Int, month: YearMonth) {
        var toStore = productsToStore
        for storage in storages {
            guard toStore > 0 else { break }
            let capacity = storage.getAvailableCapacity(month)
            toStore -= storage.storeProducts(min(toStore, capacity), month: month)
        }
    }

    public static func distributeRawMaterialAcrossStorages(_ storages: [Storage], amount: Int, month: YearMonth, unitPrice: Money) {
        let rawMaterialStored = getTotalRawMaterialStored(storages)
        let currentValue = getRawMaterialValue(storages)
        let newValue = Money.add(currentValue, unitPrice.multiply(amount))
        let newAveragePrice = newValue!.divide(rawMaterialStored + amount)
        var toStore = amount
        for storage in storages {
            guard toStore > 0 else { break }
            let capacity = storage.getAvailableCapacity(month)
            toStore -= storage.storeRawMaterials(min(toStore, capacity), month: month, unitPrice: unitPrice)
            storage.setAveragePrice(newAveragePrice)
        }
        precondition(toStore == 0, "Remainder in storing raw material must not be greater zero.")
    }

    public static func getAverageRawMaterialPrice(_ storages: [Storage]) -> Money? {
        let value = getRawMaterialValue(storages)
        let inventory = getTotalRawMaterialStored(storages)
        if let value = value {
            return inventory > 0 ? value.divide(inventory) : value
        }
        return nil
    }

    public static func getProductsStored(_ storages: [Storage]) -> Int {
        storages.reduce(0) { $0 + $1.storedProducts }
    }

    public static func getRawMaterialStored(_ storages: [Storage]) -> Int {
        storages.reduce(0) { $0 + $1.storedRawMaterials }
    }

    public static func getRawMaterialValue(_ storages: [Storage]) -> Money? {
        var result: Money? = nil
        for storage in storages {
            result = Money.add(result, storage.getValue())
        }
        return result
    }

    public static func getTotalRawMaterialStored(_ storages: [Storage]) -> Int {
        storages.reduce(0) { $0 + $1.storedRawMaterials }
    }

    public static func getTotalStored(_ storages: [Storage]) -> Int {
        storages.reduce(0) { $0 + $1.getTotalStored() }
    }

    public static func removeProductsFromStorages(_ storages: [Storage], productsToRemove: Int) {
        var toRemove = productsToRemove
        for storage in storages {
            guard toRemove > 0 else { break }
            toRemove -= storage.removeProducts(productsToRemove)
        }
        precondition(toRemove == 0, "Remainder in removing product must not be greater zero.")
    }

    public static func removeRawMaterialFromStorages(_ storages: [Storage], rawMaterialToRemove: Int) -> Int {
        var toRemove = rawMaterialToRemove
        var result = 0
        for storage in storages {
            guard toRemove > 0 else { break }
            let removed = storage.removeRawMaterials(toRemove)
            toRemove -= removed
            result += removed
        }
        return result
    }
}
