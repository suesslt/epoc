package com.jore.epoc.bo;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
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

    @Override
    public String toString() {
        return "CompanySimulationStep [simulationStep.isOpen=" + simulationStep.isOpen() + ", company=" + company.getName() + ", isOpen=" + isOpen + "]";
    }
}
