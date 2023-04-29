package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.jore.epoc.bo.Company.MonthlySale;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class CompanySimulationStep extends BusinessObject {
    @ManyToOne(optional = false)
    private SimulationStep simulationStep;
    @ManyToOne(optional = false)
    private Company company;
    private boolean isOpen;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "companySimulationStep", orphanRemoval = true)
    private List<DistributionStep> distributionSteps = new ArrayList<>();

    public void addDistributionStep(DistributionStep distributionStep) {
        distributionStep.setCompanySimulationStep(this);
        distributionSteps.add(distributionStep);
    }

    public void finish() {
        simulationStep.getSimulation().finishCompanyStep(this);
    }

    public Company getCompany() {
        return company;
    }

    public SimulationStep getSimulationStep() {
        return simulationStep;
    }

    public List<MonthlySale> getSoldProductsPerMonth() {
        List<MonthlySale> result = new ArrayList<>();
        for (DistributionStep distributionStep : distributionSteps) {
            YearMonth simulationMonth = simulationStep.getSimulationMonth();
            Market market = distributionStep.getDistributionInMarket().getMarketSimulation().getMarket();
            Integer productsSold = distributionStep.getSoldProducts();
            result.add(new MonthlySale(simulationMonth, market.getName(), productsSold));
        }
        return result;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public void setSimulationStep(SimulationStep simulationStep) {
        this.simulationStep = simulationStep;
    }
}
