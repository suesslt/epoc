package com.jore.epoc.bo;

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
public class CompanySimulationStep extends BusinessObject {
    @ManyToOne(optional = false)
    private SimulationStep simulationStep;
    @ManyToOne(optional = false)
    private Company company;
    private boolean isOpen;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "companySimulationStep", orphanRemoval = true)
    private List<SimulationEvent> simulationEvents = new ArrayList<>();

    public void addEvent(SimulationEvent simulationEvent) {
        simulationEvent.setCompanySimulationStep(this);
        simulationEvents.add(simulationEvent);
    }

    @Override
    public String toString() {
        return "CompanySimulationStep [simulationStep.isOpen=" + simulationStep.isOpen() + ", company=" + company.getName() + ", isOpen=" + isOpen + "]";
    }
}
