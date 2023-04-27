package com.jore.epoc.bo.orders;

import java.time.YearMonth;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;

public interface SimulationOrder {
    void apply(Company company);

    Company getCompany();

    YearMonth getExecutionMonth();

    Money getFixedCosts();

    Money getVariableCosts();

    boolean isExecuted();

    void setCompany(Company company);

    void setExecuted(boolean executed);
}