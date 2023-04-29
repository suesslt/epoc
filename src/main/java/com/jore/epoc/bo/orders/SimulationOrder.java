package com.jore.epoc.bo.orders;

import java.time.YearMonth;

public interface SimulationOrder {
    void execute();

    YearMonth getExecutionMonth();

    boolean isExecuted();
}