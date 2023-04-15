package com.jore.epoc.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
public class Company extends BusinessObject {
    @ManyToOne(optional = false)
    private Simulation simulation;
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company", orphanRemoval = true)
    private List<UserInCompanyRole> users = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company", orphanRemoval = true)
    private List<Factory> factories = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company", orphanRemoval = true)
    private List<CreditLine> creditLines = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company", orphanRemoval = true)
    private List<Storage> storages = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company", orphanRemoval = true)
    private List<DistributionInMarket> distributionInMarkets = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company", orphanRemoval = true)
    private List<CompanySimulationStep> companySimulationSteps = new ArrayList<>();

    public void addCompanySimulationStep(CompanySimulationStep companySimulationStep) {
        companySimulationStep.setCompany(this);
        companySimulationSteps.add(companySimulationStep);
    }

    public void addFactory(Factory factory) {
        factory.setCompany(this);
        factories.add(factory);
    }

    public UserInCompanyRole addLogin(Login login) {
        UserInCompanyRole userInCompanyRole = new UserInCompanyRole();
        userInCompanyRole.setCompany(this);
        userInCompanyRole.setUser(login);
        login.addCompanyRole(userInCompanyRole);
        users.add(userInCompanyRole);
        return userInCompanyRole;
    }

    public void addStorage(Storage storage) {
        storage.setCompany(this);
        storages.add(storage);
    }

    public CompanySimulationStep getCompanySimulationStep(SimulationStep simulationStep) {
        Optional<CompanySimulationStep> result = companySimulationSteps.stream().filter(step -> step.getSimulationStep().equals(simulationStep)).findFirst();
        if (result.isEmpty()) {
            CompanySimulationStep companySimulationStep = new CompanySimulationStep();
            addCompanySimulationStep(companySimulationStep);
            simulationStep.addCompanySimulationStep(companySimulationStep);
            companySimulationStep.setOpen(true);
            result = Optional.of(companySimulationStep);
        }
        return result.get();
    }

    public List<CreditLine> getCreditLines() {
        return Collections.unmodifiableList(creditLines);
    }

    public List<DistributionInMarket> getDistributionInMarkets() {
        return Collections.unmodifiableList(distributionInMarkets);
    }

    public List<Factory> getFactories() {
        return Collections.unmodifiableList(factories);
    }

    public List<Storage> getStorages() {
        return Collections.unmodifiableList(storages);
    }
}
