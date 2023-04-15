package com.jore.epoc.bo;

import java.time.YearMonth;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BuildStorageEvent extends SimulationEvent {
    private Integer capacity;
    private YearMonth storageStartMonth;

    @Override
    public void apply(Company company) {
        Storage storage = new Storage();
        storage.setCapacity(capacity);
        storage.setStorageStartMonth(storageStartMonth);
        company.addStorage(storage);
    }

    @Override
    public Integer getVariableNumber() {
        return capacity;
    }
}
