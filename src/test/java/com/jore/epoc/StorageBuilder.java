package com.jore.epoc;

import java.time.YearMonth;

import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.Storage;

public class StorageBuilder {
    public static StorageBuilder builder() {
        return new StorageBuilder();
    }

    private Company company;
    private int capacity;
    private YearMonth storageStartMonth;

    public Storage build() {
        Storage result = new Storage();
        result.setCapacity(capacity);
        result.setCompany(company);
        result.setStorageStartMonth(storageStartMonth);
        return result;
    }

    public StorageBuilder capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public StorageBuilder company(Company company) {
        this.company = company;
        return this;
    }

    public StorageBuilder storageStartMonth(YearMonth storageStartMonth) {
        this.storageStartMonth = storageStartMonth;
        return this;
    }
}
