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

    public void addCompany(Company company) {
        company.setSimulation(this);
        companies.add(company);
    }

    public void addSimulationStep(SimulationStep simulationStep) {
        simulationStep.setSimulation(this);
        simulationSteps.add(simulationStep);
    }

    public void finishCompanyStep(CompanySimulationStep companySimulationStep) {
        companySimulationStep.setOpen(false);
        if (companySimulationStep.getSimulationStep().areAllCompanyStepsFinished()) {
            simulate();
        }
    }

    // Can return empty Optional if simulation has finished
    public Optional<SimulationStep> getActiveSimulationStep() {
        Objects.requireNonNull(startMonth);
        Objects.requireNonNull(nrOfSteps);
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
                if (simulationStep.get().getSimulationMonth().isBefore(startMonth.plusMonths(nrOfSteps - 1))) {
                    result = Optional.of(createSimulationStep(simulationStep.get().getSimulationMonth().plusMonths(1)));
                } else {
                    isFinished = true;
                }
            }
        } else {
            isStarted = true;
            result = Optional.of(createSimulationStep(startMonth));
        }
        return result;
    }

    private SimulationStep createSimulationStep(YearMonth month) {
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

    private void simulate() {
        Optional<SimulationStep> activeSimulationStep = getActiveSimulationStep();
        activeSimulationStep.get().setOpen(false);
        for (CompanySimulationStep companySimulationStep : activeSimulationStep.get().getCompanySimulationSteps()) {
            Company company = companySimulationStep.getCompany();
            for (AbstractSimulationEvent simulationEvent : companySimulationStep.getSimulationEvents()) {
                simulationEvent.apply(company);
            }
            company.manufactureProducts(activeSimulationStep.get().getSimulationMonth());
            company.chargeStorageCost(activeSimulationStep.get().getSimulationMonth());
            company.chargeInterest(activeSimulationStep.get().getSimulationMonth());
        }
    }
}
