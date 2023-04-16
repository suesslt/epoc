package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.accounting.BookingEvent;
import com.jore.epoc.bo.accounting.InterestRateEvent;
import com.jore.epoc.bo.accounting.StorageCostEvent;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
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
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company", orphanRemoval = true)
//    private List<CreditLine> creditLines = new ArrayList<>();
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "company", orphanRemoval = true)
    private CreditLine creditLine;
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

    public void book(BookingEvent bookingEvent) {
        log.info("Booking of: " + bookingEvent);
    }

    public void chargeInterest(YearMonth simulationMonth) {
        if (creditLine != null) {
            InterestRateEvent bookingEvent = new InterestRateEvent();
            bookingEvent.setBookingText("Inerest cost for month " + simulationMonth);
            bookingEvent.setBookingDate(simulationMonth.atDay(1));
            bookingEvent.setAmount(creditLine.getMonthlyInterest());
            book(bookingEvent);
        }
    }

    public void chargeStorageCost(YearMonth simulationMonth) {
        Optional<Money> storageCost = storages.stream().map(storage -> storage.getCost()).reduce((m1, m2) -> m1.add(m2));
        if (storageCost.isPresent()) {
            StorageCostEvent bookingEvent = new StorageCostEvent();
            bookingEvent.setBookingText("Storage cost for month " + simulationMonth);
            bookingEvent.setBookingDate(simulationMonth.atDay(1));
            bookingEvent.setAmount(storageCost.get());
            book(bookingEvent);
        }
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

    public List<DistributionInMarket> getDistributionInMarkets() {
        return Collections.unmodifiableList(distributionInMarkets);
    }

    public List<Factory> getFactories() {
        return Collections.unmodifiableList(factories);
    }

    public List<Storage> getStorages() {
        return Collections.unmodifiableList(storages);
    }

    public void manufactureProducts(YearMonth productionMonth) {
        int amountProduced = 0;
        for (Factory factory : factories) {
            amountProduced += factory.produce(productionMonth);
        }
        Storage.distributeAccrossStorages(storages, amountProduced, productionMonth);
    }
}
