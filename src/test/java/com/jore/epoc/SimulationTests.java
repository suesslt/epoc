package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.Simulation;
import com.jore.epoc.bo.step.SimulationStep;

class SimulationTests {
    @Test
    public void testEmptySimulation() {
        Simulation simulation = new Simulation();
        assertThrows(NullPointerException.class, () -> {
            simulation.getActiveSimulationStep();
        });
    }

    @Test
    public void testFirstCall() {
        Simulation simulation = new Simulation();
        simulation.setId(42);
        simulation.setIsFinished(false);
        simulation.setIsStarted(false);
        simulation.setName("Test Simulation");
        simulation.setStartMonth(YearMonth.of(2000, 1));
        simulation.setNrOfMonths(12);
        Optional<SimulationStep> activeSimulationStep = simulation.getActiveSimulationStep();
        assertTrue(activeSimulationStep.isPresent());
        assertEquals(42, activeSimulationStep.get().getSimulation().getId());
        assertEquals(YearMonth.of(2000, 1), activeSimulationStep.get().getSimulationMonth());
    }

    @Test
    public void testLastCall() {
        Simulation simulation = new Simulation();
        simulation.setId(42);
        simulation.setIsFinished(false);
        simulation.setIsStarted(false);
        simulation.setName("Test Simulation");
        simulation.setStartMonth(YearMonth.of(2000, 1));
        simulation.setNrOfMonths(12);
        simulation.addSimulationStep(createSimulationStep(YearMonth.of(2000, 12), false));
        assertFalse(simulation.getActiveSimulationStep().isPresent());
    }

    @Test
    public void testWithinSimulation() {
        Simulation simulation = new Simulation();
        simulation.setId(42);
        simulation.setIsFinished(false);
        simulation.setIsStarted(false);
        simulation.setName("Test Simulation");
        simulation.setStartMonth(YearMonth.of(2000, 1));
        simulation.setNrOfMonths(12);
        simulation.addSimulationStep(createSimulationStep(YearMonth.of(2000, 1), false));
        simulation.addSimulationStep(createSimulationStep(YearMonth.of(2000, 2), false));
        simulation.addSimulationStep(createSimulationStep(YearMonth.of(2000, 3), false));
        simulation.addSimulationStep(createSimulationStep(YearMonth.of(2000, 4), false));
        simulation.addSimulationStep(createSimulationStep(YearMonth.of(2000, 5), true));
        Optional<SimulationStep> activeSimulationStep = simulation.getActiveSimulationStep();
        assertTrue(activeSimulationStep.isPresent());
        assertEquals(YearMonth.of(2000, 5), activeSimulationStep.get().getSimulationMonth());
    }

    @Test
    public void testWithinSimulationLastStepClosed() {
        Simulation simulation = new Simulation();
        simulation.setId(42);
        simulation.setIsFinished(false);
        simulation.setIsStarted(false);
        simulation.setName("Test Simulation");
        simulation.setStartMonth(YearMonth.of(2000, 1));
        simulation.setNrOfMonths(12);
        simulation.addSimulationStep(createSimulationStep(YearMonth.of(2000, 1), false));
        simulation.addSimulationStep(createSimulationStep(YearMonth.of(2000, 2), false));
        simulation.addSimulationStep(createSimulationStep(YearMonth.of(2000, 3), false));
        simulation.addSimulationStep(createSimulationStep(YearMonth.of(2000, 4), false));
        simulation.addSimulationStep(createSimulationStep(YearMonth.of(2000, 5), false));
        simulation.addCompany(createCompany("Company A"));
        simulation.addCompany(createCompany("Company B"));
        simulation.addCompany(createCompany("Company C"));
        Optional<SimulationStep> activeSimulationStep = simulation.getActiveSimulationStep();
        assertTrue(activeSimulationStep.isPresent());
        assertEquals(YearMonth.of(2000, 6), activeSimulationStep.get().getSimulationMonth());
        assertEquals(3, activeSimulationStep.get().getCompanySimulationSteps().size());
    }

    private Company createCompany(String name) {
        Company result = new Company();
        result.setName(name);
        return result;
    }

    private SimulationStep createSimulationStep(YearMonth month, boolean isOpen) {
        SimulationStep result = new SimulationStep();
        result.setOpen(isOpen);
        result.setSimulationMonth(month);
        return result;
    }
}
