package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.hibernate.annotations.CompositeType;
import org.hibernate.annotations.Type;

import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.epoc.bo.orders.SimulationOrder;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Entity
public class Simulation extends BusinessObject {
    private String name;
    private YearMonth startMonth;
    private Integer nrOfSteps;
    private boolean isStarted = false;
    private boolean isFinished = false;
    @Type(com.jore.datatypes.hibernate.PercentUserType.class)
    private Percent interestRate;
    @ManyToOne(optional = false)
    private Login owner;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "simulation", orphanRemoval = true)
    private List<Company> companies = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "simulation", orphanRemoval = true)
    private List<SimulationStep> simulationSteps = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "simulation", orphanRemoval = true)
    private List<MarketSimulation> marketSimulations = new ArrayList<>();
    @AttributeOverride(name = "amount", column = @Column(name = "maintenance_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "maintenance_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money buildingMaintenanceCost;
    @Type(com.jore.datatypes.hibernate.PercentUserType.class)
    private Percent depreciationRate;

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
        log.info(String.format("Finished company step for company '%s' (%d) in simulation '%s' (%d).", companySimulationStep.getCompany().getName(), companySimulationStep.getCompany().getId(), name, getId()));
        companySimulationStep.setOpen(false);
        if (companySimulationStep.getSimulationStep().areAllCompanyStepsFinished()) {
            simulate(companySimulationStep.getSimulationStep().getSimulationMonth());
            companySimulationStep.getSimulationStep().setOpen(false);
            setSimulationToFinishedIfThisWasTheLastStep(companySimulationStep.getSimulationStep());
        }
    }

    /**
     * Will return empty Optional if simulation has finished
     */
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
                    log.info(String.format("Simulation '%s' (%d) has finished.", name, getId()));
                    isFinished = true;
                }
            }
        } else {
            log.info(String.format("Start simulation '%s' (%d).", name, getId()));
            isStarted = true;
            result = Optional.of(createSimulationStep(Objects.requireNonNull(startMonth)));
        }
        return result;
    }

    public Money getBuildingMaintenanceCost() {
        return buildingMaintenanceCost;
    }

    public Percent getDepreciationRate() {
        return depreciationRate;
    }

    public Percent getInterestRate() {
        return interestRate;
    }

    public List<MarketSimulation> getMarketSimulations() {
        return Collections.unmodifiableList(marketSimulations);
    }

    public String getName() {
        return name;
    }

    public Integer getNrOfSteps() {
        return nrOfSteps;
    }

    public Login getOwner() {
        return owner;
    }

    public Integer getSoldProducts() {
        return marketSimulations.stream().mapToInt(marketSimulation -> marketSimulation.getSoldProducts()).sum();
    }

    public YearMonth getStartMonth() {
        return startMonth;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setBuildingMaintenanceCost(Money buildingMaintenanceCost) {
        this.buildingMaintenanceCost = buildingMaintenanceCost;
    }

    public void setDepreciationRate(Percent depreciationRate) {
        this.depreciationRate = depreciationRate;
    }

    public void setInterestRate(Percent interestRate) {
        this.interestRate = interestRate;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public void setIsStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNrOfSteps(Integer nrOfSteps) {
        this.nrOfSteps = nrOfSteps;
    }

    public void setOwner(Login owner) {
        this.owner = owner;
    }

    public void setStartMonth(YearMonth startMonth) {
        this.startMonth = startMonth;
    }

    private SimulationStep createSimulationStep(YearMonth month) {
        log.debug(String.format("Simulation step created for simulation '%s' (%d) and month '%s'", name, getId(), month));
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

    private void setSimulationToFinishedIfThisWasTheLastStep(SimulationStep simulationStep) {
        if (!simulationStep.getSimulationMonth().isBefore(Objects.requireNonNull(startMonth, "Start month must not be null.").plusMonths(Objects.requireNonNull(nrOfSteps, "Number of steps must not be null") - 1))) {
            isFinished = true;
        }
    }

    private void simulate(YearMonth simulationMonth) {
        log.info(String.format("All company steps finished for simulation '%s' (%d) and month '%s'. Starting to simulate...", name, getId(), simulationMonth));
        SimulationStep simulationStep = getActiveSimulationStep().get();
        for (CompanySimulationStep companySimulationStep : simulationStep.getCompanySimulationSteps()) {
            Company company = companySimulationStep.getCompany();
            for (SimulationOrder simulationOrder : company.getOrdersForExecutionIn(simulationMonth)) {
                simulationOrder.execute();
            }
            company.chargeInterest(simulationStep.getSimulationMonth());
            company.depreciate(simulationStep.getSimulationMonth());
            company.manufactureProducts(simulationStep.getSimulationMonth());
            company.chargeBuildingMaintenanceCosts(simulationStep.getSimulationMonth());
            company.chargeWorkforceCost(simulationStep.getSimulationMonth());
        }
        for (MarketSimulation marketSimulation : simulationStep.getSimulation().getMarketSimulations()) {
            marketSimulation.simulateMarket(simulationMonth);
        }
    }
}
