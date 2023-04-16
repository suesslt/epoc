package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
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
        int remainder = productsToStore;
        Iterator<Storage> iter = storages.iterator();
        while (remainder > 0 && iter.hasNext()) {
            Storage storage = iter.next();
            int capacity = storage.getAvailableCapacity(storageMonth);
            remainder -= storage.storeProducts(Math.min(remainder, capacity), storageMonth);
        }
        // TODO Create notification if remainder bigger 0
    }

    public static void distributeRawMaterialAccrossStorages(List<Storage> storages, int rawMaterialToStore, YearMonth storageMonth) {
        int remainder = rawMaterialToStore;
        Iterator<Storage> iter = storages.iterator();
        while (remainder > 0 && iter.hasNext()) {
            Storage storage = iter.next();
            int capacity = storage.getAvailableCapacity(storageMonth);
            remainder -= storage.storeRawMaterials(Math.min(remainder, capacity), storageMonth);
        }
        // TODO Create notification if remainder bigger 0
    }

    @ManyToOne(optional = false)
    private Company company;
    private YearMonth storageStartMonth;
    private int capacity;
    @Builder.Default
    private int storedProducts = 0;
    @Builder.Default
    private int storedRawMaterials = 0;
    @AttributeOverride(name = "amount", column = @Column(name = "storage_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "storage_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money storageCostPerUnitAndMonth;

    public int getAvailableCapacity(YearMonth storageMonth) {
        return isBuiltAndReady(storageMonth) ? capacity - getTotalStored() : 0;
    }

    public Money getCost() {
        Objects.requireNonNull(storageCostPerUnitAndMonth);
        return storageCostPerUnitAndMonth.multiply(getTotalStored());
    }

    public int getTotalStored() {
        return storedProducts + storedRawMaterials;
    }

    public boolean isBuiltAndReady(YearMonth storageMonth) {
        return !storageMonth.isBefore(storageStartMonth);
    }

    public int removeProducts(int productsToRemove) {
        int removed = Math.min(storedProducts, productsToRemove);
        storedProducts -= removed;
        return removed;
    }

    public int removeRawMaterials(int rawMaterialToRemove) {
        int removed = Math.min(storedProducts, rawMaterialToRemove);
        storedProducts -= removed;
        return removed;
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
