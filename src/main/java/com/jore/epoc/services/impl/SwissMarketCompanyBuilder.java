package com.jore.epoc.services.impl;

import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.CompanyBuilder;
import com.jore.epoc.bo.DistributionInMarket;
import com.jore.epoc.bo.MarketSimulation;
import com.jore.epoc.bo.StandardCompanyBuilder;

public class SwissMarketCompanyBuilder extends StandardCompanyBuilder implements CompanyBuilder {
    private MarketSimulation marketSimulation;

    @Override
    public Company build() {
        Company company = super.build();
        DistributionInMarket distributionInMarket = new DistributionInMarket();
        distributionInMarket.setCompany(company);
        distributionInMarket.setMarketSimulation(marketSimulation);
        company.addDistributionInMarket(distributionInMarket);
        return company;
    }
}
