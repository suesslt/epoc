package com.jore.epoc;

import java.time.YearMonth;

import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.Factory;

public class FactoryBuilder {
    public static FactoryBuilder builder() {
        return new FactoryBuilder();
    }

    private Company company;
    private YearMonth productionStartMonth;
    private int dailyCapacityPerProductionLine;
    private int productionLines;

    public Factory build() {
        Factory result = new Factory();
        result.setCompany(company);
        result.setProductionStartMonth(productionStartMonth);
        result.setDailyCapacityPerProductionLine(dailyCapacityPerProductionLine);
        result.setProductionLines(productionLines);
        return result;
    }

    public FactoryBuilder company(Company company) {
        this.company = company;
        return this;
    }

    public FactoryBuilder dailyCapacityPerProductionLine(int dailyCapacityPerProductionLine) {
        this.dailyCapacityPerProductionLine = dailyCapacityPerProductionLine;
        return this;
    }

    public FactoryBuilder productionLines(int productionLines) {
        this.productionLines = productionLines;
        return this;
    }

    public FactoryBuilder productionStartMonth(YearMonth productionStartMonth) {
        this.productionStartMonth = productionStartMonth;
        return this;
    }
}
