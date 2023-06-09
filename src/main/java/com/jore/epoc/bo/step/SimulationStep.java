package com.jore.epoc.bo.step;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.annotations.Type;

import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.Simulation;
import com.jore.jpa.AbstractBusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class SimulationStep extends AbstractBusinessObject {
    @ManyToOne(optional = false)
    private Simulation simulation;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "simulationStep", orphanRemoval = true)
    private List<CompanySimulationStep> companySimulationSteps = new ArrayList<>();
    @Type(com.jore.datatypes.hibernate.YearMonthUserType.class)
    private YearMonth simulationMonth;
    private boolean isOpen;

    public void addCompanySimulationStep(CompanySimulationStep companySimulationStep) {
        companySimulationStep.setSimulationStep(this);
        companySimulationSteps.add(companySimulationStep);
    }

    public boolean areAllCompanyStepsFinished() {
        return companySimulationSteps.stream().filter(step -> step.isOpen()).findFirst().isEmpty();
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

    public YearMonth getSimulationMonth() {
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

    @Override
    public String toString() {
        return "SimulationStep [simulationMonth=" + simulationMonth + ", isOpen=" + isOpen + "]";
    }
}
