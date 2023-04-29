package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.jfree.util.Log;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Builder.Default
    private int storedProducts = 0;
    @Builder.Default
    private int storedRawMaterials = 0;

    public int getAvailableCapacity(YearMonth storageMonth) {
        return isBuiltAndReady(storageMonth) ? capacity - getTotalStored() : 0;
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
