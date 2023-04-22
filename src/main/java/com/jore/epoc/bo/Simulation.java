package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.jore.epoc.bo.events.AbstractSimulationEvent;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Entity
@Getter
@Setter
public class Simulation extends BusinessObject {
    private String name;
    private YearMonth startMonth;
    private Integer nrOfSteps;
    private boolean isStarted = false;
    private boolean isFinished = false;
    @ManyToOne(optional = false)
    private Login owner;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "simulation", orphanRemoval = true)
    private List<Company> companies = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "simulation", orphanRemoval = true)
    private List<SimulationStep> simulationSteps = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "simulation", orphanRemoval = true)
    private List<MarketSimulation> marketSimulations = new ArrayList<>();

    public void addCompany(Company company) {
        company.setSimulation(this);
        companies.add(company);
    }

    public void addMarketSimulation(MarketSimulation marketSimulation) {
        marketSimulation.setSimulation(this);
        marketSimulations.add(marketSimulation);
    }

    public void addSimulationStep(SimulationStep simulationStep) {
        simulationStep.setSimulation(this);
        simulationSteps.add(simulationStep);
    }

    public void finishCompanyStep(CompanySimulationStep companySimulationStep) {
        log.info(String.format("Finished company step for company '%s' (%d) in simulation '%s' (%d).", companySimulationStep.getCompany().getName(), companySimulationStep.getCompany().getId(), getName(), getId()));
        companySimulationStep.setOpen(false);
        if (companySimulationStep.getSimulationStep().areAllCompanyStepsFinished()) {
            simulate(companySimulationStep.getSimulationStep().getSimulationMonth());
        }
    }

    // Can return empty Optional if simulation has finished
    public Optional<SimulationStep> getActiveSimulationStep() {
        Optional<SimulationStep> result = Optional.empty();
        Optional<SimulationStep> simulationStep = simulationSteps.stream().sorted(new Comparator<SimulationStep>() {
            @Override
            public int compare(SimulationStep o1, SimulationStep o2) {
                return o2.getSimulationMonth().compareTo(o1.getSimulationMonth());
            }
        }).findFirst();
        if (simulationStep.isPresent()) {
            if (simulationStep.get().isOpen()) {
                result = simulationStep;
            } else {
                if (simulationStep.get().getSimulationMonth().isBefore(Objects.requireNonNull(startMonth).plusMonths(Objects.requireNonNull(nrOfSteps) - 1))) {
                    result = Optional.of(createSimulationStep(simulationStep.get().getSimulationMonth().plusMonths(1)));
                } else {
                    log.info(String.format("Simulation '%s' (%d) has finished.", getName(), getId()));
                    isFinished = true;
                }
            }
        } else {
            log.info(String.format("Start simulation '%s' (%d).", getName(), getId()));
            isStarted = true;
            result = Optional.of(createSimulationStep(Objects.requireNonNull(startMonth)));
        }
        return result;
    }

    private SimulationStep createSimulationStep(YearMonth month) {
        log.debug(String.format("Simulation step created for simulation '%s' (%d) and month '%s'", getName(), getId(), month));
        SimulationStep result = new SimulationStep();
        result.setSimulationMonth(month);
        result.setOpen(true);
        addSimulationStep(result);
        for (Company company : companies) {
            CompanySimulationStep companySimulationStep = new CompanySimulationStep();
            companySimulationStep.setOpen(true);
            company.addCompanySimulationStep(companySimulationStep);
            result.addCompanySimulationStep(companySimulationStep);
        }
        return result;
    }

    private void simulate(YearMonth simulationMonth) {
        log.info(String.format("All company steps finished for simulation '%s' (%d) and month '%s'. Starting to simulate...", getName(), getId(), simulationMonth));
        SimulationStep simulationStep = getActiveSimulationStep().get();
        simulationStep.setOpen(false);
        for (CompanySimulationStep companySimulationStep : simulationStep.getCompanySimulationSteps()) {
            Company company = companySimulationStep.getCompany();
            for (AbstractSimulationEvent simulationEvent : companySimulationStep.getSimulationEvents()) {
                simulationEvent.apply(company);
            }
            company.manufactureProducts(simulationStep.getSimulationMonth());
            company.chargeStorageCost(simulationStep.getSimulationMonth());
            company.chargeInterest(simulationStep.getSimulationMonth());
        }
        for (MarketSimulation marketSimulation : simulationStep.getSimulation().getMarketSimulations()) {
            marketSimulation.simulateMarket(simulationMonth);
        }
    }
}
