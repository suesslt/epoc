package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class SimulationStep extends BusinessObject {
    @ManyToOne(optional = false)
    private Simulation simulation;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "simulationStep", orphanRemoval = true)
    private List<CompanySimulationStep> companySimulationSteps = new ArrayList<>();
    private YearMonth simulationMonth;
    private boolean isOpen;

    public void addCompanySimulationStep(CompanySimulationStep companySimulationStep) {
        companySimulationStep.setSimulationStep(this);
        companySimulationSteps.add(companySimulationStep);
    }

    public boolean areAllCompanyStepsFinished() {
        return companySimulationSteps.stream().filter(step -> (step.isOpen() == true)).findFirst().isEmpty();
    }

    public CompanySimulationStep getCompanySimulationStepFor(Company company) {
        return companySimulationSteps.stream().filter(step -> step.getCompany().equals(company)).findFirst().get();
    }

    public List<CompanySimulationStep> getCompanySimulationSteps() {
        return Collections.unmodifiableList(companySimulationSteps);
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public final YearMonth getSimulationMonth() {
        return simulationMonth;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public void setSimulationMonth(YearMonth simulationMonth) {
        this.simulationMonth = simulationMonth;
    }
}
