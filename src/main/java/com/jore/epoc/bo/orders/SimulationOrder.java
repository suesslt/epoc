package com.jore.epoc.bo.orders;

import java.time.YearMonth;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;

public interface SimulationOrder {
    Company getCompany();

    YearMonth getExecutionMonth();

    Money getFixedCosts();

    Money getVariableCosts();

    boolean isExecuted();

    void apply(Company company);
}