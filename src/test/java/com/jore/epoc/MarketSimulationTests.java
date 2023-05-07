package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.YearMonth;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.step.CompanySimulationStep;
import com.jore.epoc.bo.step.SimulationStep;

class MarketSimulationTests {
    private static final String CHF = "CHF";

    @Test
    public void testSoldProductsFullDistributionForOneCompany() {
        SimulationBuilder builder = SimulationBuilder.builder().numberOfSimulationSteps(100).marketSize(100000);
        builder.increaseCreditLine(YearMonth.of(2020, 1), Money.of(CHF, 1000000000));
        builder.buildStorage(YearMonth.of(2020, 1), 1000000);
        builder.buyRawMaterial(YearMonth.of(2020, 1), 1000000);
        builder.buildFactory(YearMonth.of(2020, 1));
        builder.enterMarket(YearMonth.of(2020, 1));
        Company company = builder.build();
        Optional<SimulationStep> activeSimulationStep = company.getSimulation().getActiveSimulationStep();
        while (activeSimulationStep.isPresent()) {
            for (CompanySimulationStep companySimulationStep : activeSimulationStep.get().getCompanySimulationSteps()) {
                companySimulationStep.finish();
                ;
            }
            activeSimulationStep = company.getSimulation().getActiveSimulationStep();
        }
        assertEquals(96407, company.getSimulation().getMarketSimulations().get(0).getSoldProducts());
        assertEquals(96407, company.getSoldProducts());
    }

    @Test
    public void testSoldProductsFullDistributionForTwoCompanies() {
        SimulationBuilder builder = SimulationBuilder.builder().numberOfSimulationSteps(100).marketSize(100000);
        builder.increaseCreditLine(YearMonth.of(2020, 1), Money.of(CHF, 1000000000));
        builder.buildStorage(YearMonth.of(2020, 1), 1000000);
        builder.buyRawMaterial(YearMonth.of(2020, 1), 1000000);
        builder.buildFactory(YearMonth.of(2020, 1));
        builder.enterMarket(YearMonth.of(2020, 1));
        Company companyA = builder.build();
        builder = SimulationBuilder.builder().simulation(companyA.getSimulation()).numberOfSimulationSteps(100);
        builder.increaseCreditLine(YearMonth.of(2020, 1), Money.of(CHF, 1000000000));
        builder.buildStorage(YearMonth.of(2020, 1), 1000000);
        builder.buyRawMaterial(YearMonth.of(2020, 1), 1000000);
        builder.buildFactory(YearMonth.of(2020, 1));
        builder.enterMarket(YearMonth.of(2020, 1));
        Company companyB = builder.build();
        Optional<SimulationStep> activeSimulationStep = companyA.getSimulation().getActiveSimulationStep();
        while (activeSimulationStep.isPresent()) {
            for (CompanySimulationStep companySimulationStep : activeSimulationStep.get().getCompanySimulationSteps()) {
                companySimulationStep.finish();
            }
            activeSimulationStep = companyA.getSimulation().getActiveSimulationStep();
        }
        assertEquals(99998, companyA.getSimulation().getMarketSimulations().get(0).getSoldProducts());
        assertEquals(49999, companyA.getSoldProducts());
        assertEquals(49999, companyB.getSoldProducts());
    }
}
