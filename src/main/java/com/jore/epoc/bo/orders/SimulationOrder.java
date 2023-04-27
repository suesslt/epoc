package com.jore.epoc.bo.orders;

import java.time.YearMonth;

import com.jore.epoc.bo.Company;

public interface SimulationOrder {
    void apply();

    Company getCompany();

    YearMonth getExecutionMonth();

    boolean isExecuted();

    void setCompany(Company company);

    void setExecuted(boolean executed);
}