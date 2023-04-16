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
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Storage extends BusinessObject {
    // TODO Write test cases
    public static void distributeAccrossStorages(List<Storage> storages, int amountProduced, YearMonth storageMonth) {
        int remainder = amountProduced;
        Iterator<Storage> iter = storages.iterator();
        while (remainder > 0 && iter.hasNext()) {
            Storage storage = iter.next();
            int capacity = storage.getAvailableCapacity(storageMonth);
            remainder -= storage.store(Math.min(remainder, capacity));
        }
        // TODO Throw error if remainder bigger 0
    }

    @ManyToOne(optional = false)
    private Company company;
    private YearMonth storageStartMonth;
    private int capacity;
    private int stored = 0;
    @AttributeOverride(name = "amount", column = @Column(name = "storage_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "storage_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money storageCostPerUnitAndMonth;

    public int getAvailableCapacity(YearMonth storageMonth) {
        return !storageStartMonth.isBefore(storageMonth) ? capacity - stored : 0;
    }

    public Money getCost() {
        Objects.requireNonNull(storageCostPerUnitAndMonth);
        return storageCostPerUnitAndMonth.multiply(getStored());
    }

    public int remove(int amountToRemove) {
        int removed = Math.min(stored, amountToRemove);
        stored -= removed;
        return removed;
    }

    public int store(int amountToStore) {
        stored += amountToStore;
        return amountToStore;
    }
}
