package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CompositeType;

import com.jore.Assert;
import com.jore.datatypes.money.Money;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Storage extends BusinessObject {
    public static void distributeProductAccrossStorages(List<Storage> storages, int productsToStore, YearMonth storageMonth) {
        int toStore = productsToStore;
        Iterator<Storage> iter = storages.iterator();
        while (toStore > 0 && iter.hasNext()) {
            Storage storage = iter.next();
            int capacity = storage.getAvailableCapacity(storageMonth);
            toStore -= storage.storeProducts(Math.min(toStore, capacity), storageMonth);
        }
    }

    public static void distributeRawMaterialAccrossStorages(List<Storage> storages, int rawMaterialToStore, YearMonth storageMonth, Money unitPrice) {
        int rawMaterialStored = getTotalRawMaterialStored(storages);
        Money currentValue = getRawMaterialValue(storages);
        Money newValue = Money.add(currentValue, unitPrice.multiply(rawMaterialToStore));
        Money newAveragePrice = newValue.divide(rawMaterialStored + rawMaterialToStore);
        int toStore = rawMaterialToStore;
        Iterator<Storage> iter = storages.iterator();
        while (toStore > 0 && iter.hasNext()) {
            Storage storage = iter.next();
            int capacity = storage.getAvailableCapacity(storageMonth);
            toStore -= storage.storeRawMaterials(Math.min(toStore, capacity), storageMonth, unitPrice);
            storage.setAveragePrice(newAveragePrice);
        }
        Assert.isTrue("Remainder in storing raw material must not be greater zero.", toStore == 0);
    }

    public static Money getAverageRawMaterialPrice(List<Storage> storages) {
        Money value = getRawMaterialValue(storages);
        int inventory = getTotalRawMaterialStored(storages);
        return value != null ? (inventory > 0 ? value.divide(inventory) : value) : null; // TODO Test case
    }

    public static Integer getProductsStored(List<Storage> storages) {
        return storages.stream().mapToInt(storage -> storage.getStoredProducts()).sum();
    }

    public static Integer getRawMaterialStored(List<Storage> storages) {
        return storages.stream().mapToInt(storage -> storage.getStoredRawMaterials()).sum();
    }

    public static Money getRawMaterialValue(List<Storage> storages) {
        Money result = null;
        for (Storage storage : storages) {
            result = Money.add(result, storage.getValue());
        }
        return result;
    }

    public static int getTotalRawMaterialStored(List<Storage> storages) {
        return storages.stream().mapToInt(storage -> storage.getStoredRawMaterials()).sum();
    }

    public static Integer getTotalStored(List<Storage> storages) {
        return storages.stream().mapToInt(storage -> storage.getTotalStored()).sum();
    }

    public static void removeProductsFromStorages(List<Storage> storages, int productsToRemove) {
        int toRemove = productsToRemove;
        Iterator<Storage> iter = storages.iterator();
        while (toRemove > 0 && iter.hasNext()) {
            toRemove -= iter.next().removeProducts(productsToRemove);
        }
        Assert.isTrue("Remainder in removing product must not be greater zero.", toRemove == 0);
    }

    public static int removeRawMaterialFromStorages(List<Storage> storages, int rawMaterialToRemove) {
        int toRemove = rawMaterialToRemove;
        int result = 0;
        Iterator<Storage> storageIterator = storages.iterator();
        while (toRemove > 0 && storageIterator.hasNext()) {
            int removed = storageIterator.next().removeRawMaterials(toRemove);
            toRemove -= removed;
            result += removed;
        }
        return result;
    }

    @ManyToOne(optional = false)
    private Company company;
    private YearMonth storageStartMonth;
    private int capacity;
    private int storedProducts = 0;
    private int storedRawMaterials = 0;
    @AttributeOverride(name = "amount", column = @Column(name = "inventory_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "inventory_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money inventoryManagementCost;
    @AttributeOverride(name = "amount", column = @Column(name = "average_price_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "average_price_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money averagePrice;

    public int getAvailableCapacity(YearMonth storageMonth) {
        return isBuiltAndReady(storageMonth) ? capacity - getTotalStored() : 0;
    }

    public Money getInventoryManagementCost() {
        return inventoryManagementCost;
    }

    public Integer getStoredProducts() {
        return storedProducts;
    }

    public Integer getStoredRawMaterials() {
        return storedRawMaterials;
    }

    public int getTotalStored() {
        return storedProducts + storedRawMaterials;
    }

    public int removeProducts(int productsToRemove) {
        int result = Math.min(storedProducts, productsToRemove);
        storedProducts -= result;
        return result;
    }

    public int removeRawMaterials(int rawMaterialToRemove) {
        int result = Math.min(storedRawMaterials, rawMaterialToRemove);
        storedRawMaterials -= result;
        return result;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setInventoryManagementCost(Money inventoryManagementCost) {
        this.inventoryManagementCost = inventoryManagementCost;
    }

    public void setStorageStartMonth(YearMonth storageStartMonth) {
        this.storageStartMonth = storageStartMonth;
    }

    public int storeProducts(int productsToStore, YearMonth storeMonth) {
        int result = Math.min(productsToStore, getAvailableCapacity(storeMonth));
        storedProducts += result;
        return result;
    }

    public int storeRawMaterials(int rawMaterialToStore, YearMonth storeMonth, Money unitPrice) {
        int result = Math.min(rawMaterialToStore, getAvailableCapacity(storeMonth));
        storedRawMaterials += result;
        return result;
    }

    private Money getValue() {
        return averagePrice != null ? averagePrice.multiply(storedRawMaterials) : null;
    }

    private boolean isBuiltAndReady(YearMonth storageMonth) {
        return !Objects.requireNonNull(storageMonth, "Storage Month must not be null.").isBefore(Objects.requireNonNull(storageStartMonth, "Storage start month must not be null."));
    }

    private void setAveragePrice(Money averagePrice) {
        this.averagePrice = averagePrice;
    }
}
