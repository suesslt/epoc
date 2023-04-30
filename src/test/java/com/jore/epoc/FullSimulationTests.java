package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.YearMonth;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.step.CompanySimulationStep;
import com.jore.epoc.bo.step.SimulationStep;

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
        assertEquals(Money.of("CHF", -2361177.91), company.getAccounting().getPnL());
        assertEquals(Money.of("CHF", -2361177.91), company.getAccounting().getOwnersCapital());
        assertEquals(Money.of("CHF", -16528245.37), company.getAccounting().getCompanyValue());
    }

    @Test
    public void testOneCompanyTwelveSteps() {
        CompanyBuilder companyBuilder = CompanyBuilder.builder().name("A Company").simulationName("Full simulation").numberOfSimulationSteps(12);
        companyBuilder = companyBuilder.increaseCreditLine(YearMonth.of(2020, 1), Money.of("CHF", 33600000));
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
        assertEquals(Money.of("CHF", -10594326.51), company.getAccounting().getPnL());
        assertEquals(Money.of("CHF", -10594326.51), company.getAccounting().getOwnersCapital());
        assertEquals(Money.of("CHF", -74160285.57), company.getAccounting().getCompanyValue());
    }

    @Test
    public void testTwoCompaniesEquallyTwelveSteps() {
        CompanyBuilder companyBuilder = CompanyBuilder.builder().name("A Company").simulationName("Full simulation").numberOfSimulationSteps(12);
        companyBuilder = companyBuilder.increaseCreditLine(YearMonth.of(2020, 1), Money.of("CHF", 33600000));
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
        companyBuilder = companyBuilder.increaseCreditLine(YearMonth.of(2020, 1), Money.of("CHF", 33600000));
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
        log.info(companyA.getAccounting().toString());
        assertEquals(10000, companyA.getSoldProducts());
        assertEquals(Money.of("CHF", -10644326.51), companyA.getAccounting().getPnL());
        assertEquals(Money.of("CHF", -10644326.51), companyA.getAccounting().getOwnersCapital());
        assertEquals(Money.of("CHF", -74510285.57), companyA.getAccounting().getCompanyValue());
        assertEquals(10000, companyB.getSoldProducts());
        assertEquals(Money.of("CHF", -10644326.51), companyB.getAccounting().getPnL());
        assertEquals(Money.of("CHF", -10644326.51), companyB.getAccounting().getOwnersCapital());
        assertEquals(Money.of("CHF", -74510285.57), companyB.getAccounting().getCompanyValue());
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
