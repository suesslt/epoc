package com.jore.epoc.bo;

import java.time.YearMonth;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BuildFactoryEvent extends SimulationEvent {
    private Integer productionLines;
    private YearMonth productionStartMonth;

    @Override
    public void apply(Company company) {
        Factory factory = new Factory();
        factory.setProductionLines(productionLines);
        factory.setProductionStartMonth(productionStartMonth);
        company.addFactory(factory);
    }

    @Override
    public Integer getVariableNumber() {
        return productionLines;
    }
}
