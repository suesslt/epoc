package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
    private boolean isStarted;
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Login user;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "simulation", orphanRemoval = true)
    private List<Company> companies = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "simulation", orphanRemoval = true)
    private List<SimulationStep> simulationSteps = new ArrayList<>();

    public void addCompany(Company company) {
        company.setSimulation(this);
        companies.add(company);
    }

    public SimulationStep getCurrentSimulationStep() {
        Optional<SimulationStep> result = simulationSteps.stream().filter(step -> step.isOpen()).findFirst();
        if (result.isEmpty()) {
            result = Optional.of(createNextSimulationStep());
        }
        return result.get();
    }

    public void runSimulationStep() {
        log.info("*** this is where the logic sits!!!");
    }

    private SimulationStep createNextSimulationStep() {
        SimulationStep result = new SimulationStep();
        Optional<YearMonth> max = simulationSteps.stream().map(step -> step.getSimulationMonth()).max(new Comparator<YearMonth>() {
            @Override
            public int compare(YearMonth o1, YearMonth o2) {
                return o1.compareTo(o2);
            }
        });
        if (max.isPresent()) {
            result.setSimulationMonth(max.get().plusMonths(1));
        } else {
            result.setSimulationMonth(startMonth.plusMonths(1));
        }
        result.setSimulation(this);
        simulationSteps.add(result);
        setStarted(true);
        return result;
    }
}
