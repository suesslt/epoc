package com.jore.epoc.bo;

import com.jore.datatypes.currency.Currency;
import com.jore.epoc.bo.accounting.FinancialAccounting;

public class StandardCompanyBuilder implements CompanyBuilder {
    private Currency baseCurrency;
    private String name;
    private Simulation simulation;
    private FinancialAccounting financialAccounting = new FinancialAccounting();
    private double marketingFactor = 1.0d;
    private double productivityFactor = 1.0d;
    private double qualityFactor = 1.0d;

    @Override
    public CompanyBuilder baseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
        return this;
    }

    @Override
    public Company build() {
        Company result = new Company();
        result.setAccounting(financialAccounting);
        result.setBaseCurrency(baseCurrency);
        result.setMarketingFactor(marketingFactor);
        result.setName(name);
        result.setProductivityFactor(productivityFactor);
        result.setQualityFactor(qualityFactor);
        result.setSimulation(simulation);
        return result;
    }

    @Override
    public CompanyBuilder builder() {
        return this;
    }

    @Override
    public CompanyBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public CompanyBuilder simulation(Simulation simulation) {
        this.simulation = simulation;
        return this;
    }
}
