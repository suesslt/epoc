package com.jore.epoc.bo;

import com.jore.datatypes.currency.Currency;

public interface CompanyBuilder {
    CompanyBuilder baseCurrency(Currency baseCurrency);

    Company build();

    CompanyBuilder builder();

    CompanyBuilder name(String name);

    CompanyBuilder simulation(Simulation simulation);
}
