package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CompositeType;
import org.jfree.util.Log;

import com.jore.datatypes.money.Money;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Storage extends BusinessObject {
    // TODO Write test cases
    public static void distributeProductAccrossStorages(List<Storage> storages, int productsToStore, YearMonth storageMonth) {
        int toStore = productsToStore;
        Iterator<Storage> iter = storages.iterator();
        while (toStore > 0 && iter.hasNext()) {
            Storage storage = iter.next();
            int capacity = storage.getAvailableCapacity(storageMonth);
            toStore -= storage.storeProducts(Math.min(toStore, capacity), storageMonth);
        }
        if (toStore > 0) {
            Log.warn("*** Remainder > 0");
        }
    }

    public static void distributeRawMaterialAccrossStorages(List<Storage> storages, int rawMaterialToStore, YearMonth storageMonth) {
        int toStore = rawMaterialToStore;
        Iterator<Storage> iter = storages.iterator();
        while (toStore > 0 && iter.hasNext()) {
            Storage storage = iter.next();
            int capacity = storage.getAvailableCapacity(storageMonth);
            toStore -= storage.storeRawMaterials(Math.min(toStore, capacity), storageMonth);
        }
        if (toStore > 0) {
            Log.warn("*** Remainder > 0");
        }
    }

    public static void removeProductsFromStorages(List<Storage> storages, int productsToRemove) {
        int toRemove = productsToRemove;
        Iterator<Storage> iter = storages.iterator();
        while (toRemove > 0 && iter.hasNext()) {
            toRemove -= iter.next().removeProducts(productsToRemove);
        }
        if (toRemove > 0) {
            Log.warn("*** Remainder > 0");
        }
    }

    public static void removeRawMaterialFromStorages(List<Storage> storages, int rawMaterialToRemove) {
        int toRemove = rawMaterialToRemove;
        Iterator<Storage> iter = storages.iterator();
        while (toRemove > 0 && iter.hasNext()) {
            toRemove -= iter.next().removeRawMaterials(toRemove);
        }
        if (toRemove > 0) {
            Log.warn("*** Remainder > 0");
        }
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

    public boolean isBuiltAndReady(YearMonth storageMonth) {
        return !Objects.requireNonNull(storageMonth, "Storage Month must not be null.").isBefore(Objects.requireNonNull(storageStartMonth, "Storage start month must not be null."));
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

    public int storeRawMaterials(int rawMaterialToStore, YearMonth storeMonth) {
        int result = Math.min(rawMaterialToStore, getAvailableCapacity(storeMonth));
        storedRawMaterials += result;
        return result;
    }
}
