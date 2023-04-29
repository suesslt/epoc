package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.YearMonth;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.CompanySimulationStep;
import com.jore.epoc.bo.SimulationStep;

import lombok.extern.log4j.Log4j2;

@Log4j2
class FullSimulationTests {
    @Test
    public void testOneCompanyThreeSteps() {
        CompanyBuilder companyBuilder = CompanyBuilder.builder().name("A Company").simulationName("Full simulation").numberOfSimulationSteps(3);
        companyBuilder = companyBuilder.increaseCreditLine(YearMonth.of(2020, 1), Money.of("CHF", 3110000));
        companyBuilder = companyBuilder.buildStorage(YearMonth.of(2020, 1), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 1), 1000);
        companyBuilder = companyBuilder.buildFactory(YearMonth.of(2020, 1));
        companyBuilder = companyBuilder.enterMarket(YearMonth.of(2020, 1));
        Company company = companyBuilder.build();
        Optional<SimulationStep> activeSimulationStep = company.getSimulation().getActiveSimulationStep();
        while (activeSimulationStep.isPresent()) {
            CompanySimulationStep companySimulationStep = activeSimulationStep.get().getCompanySimulationStepFor(company);
            companySimulationStep.finish();
            activeSimulationStep = company.getSimulation().getActiveSimulationStep();
        }
        log.info(company.getAccounting().toString());
        assertEquals(Money.of("CHF", 11125.01), company.getAccounting().getPnL());
        assertEquals(Money.of("CHF", 11125.01), company.getAccounting().getOwnersCapital());
        assertEquals(Money.of("CHF", 77875.07), company.getAccounting().getCompanyValue());
    }

    @Test
    public void testOneCompanyTwelveSteps() {
        CompanyBuilder companyBuilder = CompanyBuilder.builder().name("A Company").simulationName("Full simulation").numberOfSimulationSteps(12);
        companyBuilder = companyBuilder.increaseCreditLine(YearMonth.of(2020, 1), Money.of("CHF", 3360000));
        companyBuilder = companyBuilder.buildStorage(YearMonth.of(2020, 1), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 1), 1000);
        companyBuilder = companyBuilder.buildFactory(YearMonth.of(2020, 1));
        companyBuilder = companyBuilder.enterMarket(YearMonth.of(2020, 1));
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 3), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 4), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 5), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 6), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 7), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 8), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 9), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 10), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 11), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 12), 1000);
        Company company = companyBuilder.build();
        Optional<SimulationStep> activeSimulationStep = company.getSimulation().getActiveSimulationStep();
        while (activeSimulationStep.isPresent()) {
            CompanySimulationStep companySimulationStep = activeSimulationStep.get().getCompanySimulationStepFor(company);
            companySimulationStep.finish();
            activeSimulationStep = company.getSimulation().getActiveSimulationStep();
        }
        log.info(company.getAccounting().toString());
        assertEquals(11000, company.getSoldProducts());
        assertEquals(Money.of("CHF", 382000), company.getAccounting().getPnL());
        assertEquals(Money.of("CHF", 382000), company.getAccounting().getOwnersCapital());
        assertEquals(Money.of("CHF", 2674000), company.getAccounting().getCompanyValue());
    }

    @Test
    public void testTwoCompaniesEquallyTwelveSteps() {
        CompanyBuilder companyBuilder = CompanyBuilder.builder().name("A Company").simulationName("Full simulation").numberOfSimulationSteps(12);
        companyBuilder = companyBuilder.increaseCreditLine(YearMonth.of(2020, 1), Money.of("CHF", 3360000));
        companyBuilder = companyBuilder.buildStorage(YearMonth.of(2020, 1), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 1), 1000);
        companyBuilder = companyBuilder.buildFactory(YearMonth.of(2020, 1));
        companyBuilder = companyBuilder.enterMarket(YearMonth.of(2020, 1));
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 3), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 4), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 5), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 6), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 7), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 8), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 9), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 10), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 11), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 12), 1000);
        Company companyA = companyBuilder.build();
        companyBuilder = CompanyBuilder.builder().simulation(companyA.getSimulation()).name("B Company");
        companyBuilder = companyBuilder.increaseCreditLine(YearMonth.of(2020, 1), Money.of("CHF", 3360000));
        companyBuilder = companyBuilder.buildStorage(YearMonth.of(2020, 1), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 1), 1000);
        companyBuilder = companyBuilder.buildFactory(YearMonth.of(2020, 1));
        companyBuilder = companyBuilder.enterMarket(YearMonth.of(2020, 1));
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 3), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 4), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 5), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 6), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 7), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 8), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 9), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 10), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 11), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 12), 1000);
        Company companyB = companyBuilder.build();
        Optional<SimulationStep> activeSimulationStep = companyA.getSimulation().getActiveSimulationStep();
        while (activeSimulationStep.isPresent()) {
            activeSimulationStep.get().getCompanySimulationStepFor(companyA).finish();
            activeSimulationStep.get().getCompanySimulationStepFor(companyB).finish();
            activeSimulationStep = companyA.getSimulation().getActiveSimulationStep();
        }
        assertEquals(10000, companyA.getSoldProducts());
        assertEquals(Money.of("CHF", 332000), companyA.getAccounting().getPnL());
        assertEquals(Money.of("CHF", 332000), companyA.getAccounting().getOwnersCapital());
        assertEquals(Money.of("CHF", 2324000), companyA.getAccounting().getCompanyValue());
        assertEquals(10000, companyB.getSoldProducts());
        assertEquals(Money.of("CHF", 332000), companyB.getAccounting().getPnL());
        assertEquals(Money.of("CHF", 332000), companyB.getAccounting().getOwnersCapital());
        assertEquals(Money.of("CHF", 2324000), companyB.getAccounting().getCompanyValue());
    }

    @Test
    public void testTwoCompaniesFierceCompetitionTwelveSteps() {
        CompanyBuilder companyBuilder = CompanyBuilder.builder().name("A Company").simulationName("Full simulation").numberOfSimulationSteps(12).laborForce(10000);
        companyBuilder = companyBuilder.increaseCreditLine(YearMonth.of(2020, 1), Money.of("CHF", 3360000));
        companyBuilder = companyBuilder.buildStorage(YearMonth.of(2020, 1), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 1), 1000);
        companyBuilder = companyBuilder.buildFactory(YearMonth.of(2020, 1));
        companyBuilder = companyBuilder.enterMarket(YearMonth.of(2020, 1));
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 3), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 4), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 5), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 6), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 7), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 8), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 9), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 10), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 11), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 12), 1000);
        Company companyA = companyBuilder.build();
        companyBuilder = CompanyBuilder.builder().simulation(companyA.getSimulation()).name("B Company");
        companyBuilder = companyBuilder.increaseCreditLine(YearMonth.of(2020, 1), Money.of("CHF", 3360000));
        companyBuilder = companyBuilder.buildStorage(YearMonth.of(2020, 1), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 1), 1000);
        companyBuilder = companyBuilder.buildFactory(YearMonth.of(2020, 1));
        companyBuilder = companyBuilder.enterMarket(YearMonth.of(2020, 1));
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 3), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 4), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 5), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 6), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 7), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 8), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 9), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 10), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 11), 1000);
        companyBuilder = companyBuilder.buyRawMaterial(YearMonth.of(2020, 12), 1000);
        companyBuilder = companyBuilder.changeAmountAndPriceOrder(YearMonth.of(2020, 6), 1000, Money.of("CHF", 30));
        Company companyB = companyBuilder.build();
        Optional<SimulationStep> activeSimulationStep = companyA.getSimulation().getActiveSimulationStep();
        while (activeSimulationStep.isPresent()) {
            activeSimulationStep.get().getCompanySimulationStepFor(companyA).finish();
            activeSimulationStep.get().getCompanySimulationStepFor(companyB).finish();
            activeSimulationStep = companyA.getSimulation().getActiveSimulationStep();
        }
        assertEquals(532, companyA.getSoldProducts());
        assertEquals(716, companyB.getSoldProducts());
    }
}
