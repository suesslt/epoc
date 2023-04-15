package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

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
}
