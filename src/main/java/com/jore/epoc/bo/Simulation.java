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
import com.jore.epoc.bo.settings.EpocSettings;
import com.jore.epoc.bo.step.CompanySimulationStep;
import com.jore.epoc.bo.step.SimulationStep;
import com.jore.epoc.bo.user.User;
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
    private Integer nrOfMonths;
    private boolean isStarted = false;
    private boolean isFinished = false;
    private Integer passiveSteps;
    @Type(com.jore.datatypes.hibernate.PercentUserType.class)
    private Percent interestRate;
    @ManyToOne(optional = true)
    private EpocSettings settings;
    @ManyToOne(optional = false)
    private User owner;
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
    @AttributeOverride(name = "amount", column = @Column(name = "headquarter_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "headquarter_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money headquarterCost;
    @AttributeOverride(name = "amount", column = @Column(name = "production_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "production_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money productionCost;

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
            simulate(companySimulationStep.getSimulationStep());
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
                if (simulationStep.get().getSimulationMonth().isBefore(Objects.requireNonNull(startMonth).plusMonths(Objects.requireNonNull(nrOfMonths) - 1))) {
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

    public Money getHeadquarterCost() {
        return headquarterCost;
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

    public Integer getNrOfMonths() {
        return nrOfMonths;
    }

    public User getOwner() {
        return owner;
    }

    public Money getProductionCost() {
        return productionCost;
    }

    public EpocSettings getSettings() {
        return settings;
    }

    public List<SimulationStep> getSimulationSteps() {
        return Collections.unmodifiableList(simulationSteps);
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

    public void setHeadquarterCost(Money headquarterCost) {
        this.headquarterCost = headquarterCost;
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

    public void setNrOfMonths(Integer nrOfSteps) {
        this.nrOfMonths = nrOfSteps;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setPassiveSteps(Integer passiveSteps) {
        this.passiveSteps = passiveSteps;
    }

    public void setProductionCost(Money productionCost) {
        this.productionCost = productionCost;
    }

    public void setSettings(EpocSettings settings) {
        this.settings = settings;
    }

    public void setStartMonth(YearMonth startMonth) {
        this.startMonth = startMonth;
    }

    public void simulatePassiveSteps() {
        Optional<SimulationStep> activeSimulationStep = getActiveSimulationStep();
        for (int i = 0; i < passiveSteps && activeSimulationStep.isPresent(); i++) {
            for (CompanySimulationStep companySimulationStep : activeSimulationStep.get().getCompanySimulationSteps()) {
                finishCompanyStep(companySimulationStep);
            }
            activeSimulationStep = getActiveSimulationStep();
        }
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
        if (!simulationStep.getSimulationMonth().isBefore(Objects.requireNonNull(startMonth, "Start month must not be null.").plusMonths(Objects.requireNonNull(nrOfMonths, "Number of steps must not be null") - 1))) {
            isFinished = true;
        }
    }

    private void simulate(SimulationStep simulationStep) {
        log.info(String.format("All company steps finished for simulation '%s' (%d) and month '%s'. Starting to simulate...", name, getId(), simulationStep.getSimulationMonth()));
        for (CompanySimulationStep companySimulationStep : simulationStep.getCompanySimulationSteps()) {
            Company company = companySimulationStep.getCompany();
            for (SimulationOrder simulationOrder : company.getOrdersForExecutionIn(simulationStep.getSimulationMonth())) {
                simulationOrder.execute();
            }
            company.manufactureProducts(simulationStep.getSimulationMonth());
            company.chargeWorkforceCost(simulationStep.getSimulationMonth());
            company.chargeInterest(simulationStep.getSimulationMonth());
            company.chargeDepreciation(simulationStep.getSimulationMonth());
            company.chargeBuildingMaintenanceCost(simulationStep.getSimulationMonth());
        }
        for (MarketSimulation marketSimulation : simulationStep.getSimulation().getMarketSimulations()) {
            marketSimulation.simulateMarket(simulationStep.getSimulationMonth());
        }
        simulationStep.setOpen(false);
        setSimulationToFinishedIfThisWasTheLastStep(simulationStep);
    }
}
