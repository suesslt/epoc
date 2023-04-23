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
    private List<DistributionStep> distributionSteps = new ArrayList<>();

    public void addDistributionStep(DistributionStep distributionStep) {
        distributionStep.setCompanySimulationStep(this);
        distributionSteps.add(distributionStep);
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

    @Override
    public String toString() {
        return "CompanySimulationStep [simulationStep.isOpen=" + simulationStep.isOpen() + ", company=" + company.getName() + ", isOpen=" + isOpen + "]";
    }
}
