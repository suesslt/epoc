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
    public void testAlongExcel() {
        SimulationBuilder companyBuilder = SimulationBuilder.builder().name("A Company").simulationName("Full simulation").numberOfSimulationSteps(12);
        companyBuilder = companyBuilder.increaseCreditLine(YearMonth.of(2020, 1), Money.of("CHF", 13000000));
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
        assertEquals(Money.of("CHF", 5320000.00), company.getAccounting().getBankBalance());
        assertEquals(Money.of("CHF", -13000000.00), company.getAccounting().getLongTermDebt());
        assertEquals(Money.of("CHF", 2665673.44), company.getAccounting().getRealEstateBalance());
        assertEquals(Money.of("CHF", 0.00), company.getAccounting().getRawMaterialBalance());
        assertEquals(Money.of("CHF", 0.00), company.getAccounting().getProductBalance());
        assertEquals(Money.of("CHF", -5014326.56), company.getAccounting().getPnL());
        assertEquals(Money.of("CHF", -35100285.89), company.getAccounting().getCompanyValue());
    }

    @Test
    public void testOneCompanyThreeSteps() {
        SimulationBuilder companyBuilder = SimulationBuilder.builder().name("A Company").simulationName("Full simulation").numberOfSimulationSteps(3);
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
        assertEquals(Money.of("CHF", -1484611.52), company.getAccounting().getPnL());
        assertEquals(Money.of("CHF", -1484611.52), company.getAccounting().getOwnersCapital());
        assertEquals(Money.of("CHF", -10392280.66), company.getAccounting().getCompanyValue());
    }

    @Test
    public void testOneCompanyTwelveSteps() {
        SimulationBuilder companyBuilder = SimulationBuilder.builder().name("A Company").simulationName("Full simulation").numberOfSimulationSteps(12);
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
        assertEquals(Money.of("CHF", -6044326.56), company.getAccounting().getPnL());
        assertEquals(Money.of("CHF", -6044326.56), company.getAccounting().getOwnersCapital());
        assertEquals(Money.of("CHF", -42310285.89), company.getAccounting().getCompanyValue());
    }

    @Test
    public void testTenYearsInYearSteps() {
        SimulationBuilder companyBuilder = SimulationBuilder.builder().name("A Ten Year Company").simulationName("Full simulation for ten years").numberOfSimulationSteps(120).passiveSteps(11);
        companyBuilder.increaseCreditLine(YearMonth.of(2020, 3), Money.of("CHF", 100000000l));
        companyBuilder.buildStorage(YearMonth.of(2020, 6), 10000);
        companyBuilder.buyRawMaterial(YearMonth.of(2020, 9), 10000);
        companyBuilder.buildFactory(YearMonth.of(2020, 12));
        companyBuilder.enterMarket(YearMonth.of(2021, 3));
        companyBuilder.increaseCreditLine(YearMonth.of(2021, 6), Money.of("CHF", 100000000l));
        companyBuilder.buyRawMaterial(YearMonth.of(2022, 8), 10000);
        Company company = companyBuilder.build();
        Optional<SimulationStep> activeSimulationStep = company.getSimulation().getActiveSimulationStep();
        int activeStepCounter = 0;
        while (activeSimulationStep.isPresent()) {
            activeStepCounter++;
            CompanySimulationStep companySimulationStep = activeSimulationStep.get().getCompanySimulationStepFor(company);
            companySimulationStep.finish();
            activeSimulationStep = company.getSimulation().getActiveSimulationStep();
        }
        log.info(company.getAccounting().toString());
        assertEquals(Money.of("CHF", 16000000), company.getAccounting().getRevenues());
        assertEquals(10, activeStepCounter);
        assertEquals(120, company.getCompanySimulationSteps().size());
        assertEquals(120, company.getSimulation().getSimulationSteps().size());
    }

    @Test
    public void testTwoCompaniesEquallyTwelveSteps() {
        SimulationBuilder companyBuilder = SimulationBuilder.builder().name("A Company").simulationName("Full simulation").numberOfSimulationSteps(12);
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
        companyBuilder = SimulationBuilder.builder().simulation(companyA.getSimulation()).name("B Company");
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
        assertEquals(Money.of("CHF", -6544326.56), companyA.getAccounting().getPnL());
        assertEquals(Money.of("CHF", -6544326.56), companyA.getAccounting().getOwnersCapital());
        assertEquals(Money.of("CHF", -45810285.89), companyA.getAccounting().getCompanyValue());
        assertEquals(10000, companyB.getSoldProducts());
        assertEquals(Money.of("CHF", -6544326.56), companyB.getAccounting().getPnL());
        assertEquals(Money.of("CHF", -6544326.56), companyB.getAccounting().getOwnersCapital());
        assertEquals(Money.of("CHF", -45810285.89), companyB.getAccounting().getCompanyValue());
    }

    @Test
    public void testTwoCompaniesFierceCompetitionTwelveSteps() {
        SimulationBuilder companyBuilder = SimulationBuilder.builder().name("A Company").simulationName("Full simulation").numberOfSimulationSteps(12).laborForce(10000);
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
        companyBuilder = SimulationBuilder.builder().simulation(companyA.getSimulation()).name("B Company");
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
        companyBuilder = companyBuilder.changeAmountAndPriceOrder(YearMonth.of(2020, 6), 1000, Money.of("CHF", 30));
        Company companyB = companyBuilder.build();
        Optional<SimulationStep> activeSimulationStep = companyA.getSimulation().getActiveSimulationStep();
        while (activeSimulationStep.isPresent()) {
            activeSimulationStep.get().getCompanySimulationStepFor(companyA).finish();
            activeSimulationStep.get().getCompanySimulationStepFor(companyB).finish();
            activeSimulationStep = companyA.getSimulation().getActiveSimulationStep();
        }
        assertEquals(416, companyA.getSoldProducts());
        assertEquals(832, companyB.getSoldProducts());
    }
}
