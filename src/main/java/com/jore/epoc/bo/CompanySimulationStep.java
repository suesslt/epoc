package com.jore.epoc.bo;

import java.util.ArrayList;
import java.util.List;

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
public class CompanySimulationStep extends BusinessObject {
    @ManyToOne(optional = false)
    private SimulationStep simulationStep;
    @ManyToOne(optional = false)
    private Company company;
    private boolean isOpen;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "companySimulationStep", orphanRemoval = true)
    private List<AbstractSimulationEvent> simulationEvents = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "companySimulationStep", orphanRemoval = true)
    private List<DistributionStep> distributionSteps = new ArrayList<>();

    public void addDistributionStep(DistributionStep distributionStep) {
        distributionStep.setCompanySimulationStep(this);
        distributionSteps.add(distributionStep);
    }

    public void addEvent(AbstractSimulationEvent simulationEvent) {
        simulationEvent.setCompanySimulationStep(this);
        simulationEvents.add(simulationEvent);
    }

    @Override
    public String toString() {
        return "CompanySimulationStep [simulationStep.isOpen=" + simulationStep.isOpen() + ", company=" + company.getName() + ", isOpen=" + isOpen + "]";
    }
}
