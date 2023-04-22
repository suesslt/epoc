package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.accounting.BookingEvent;
import com.jore.epoc.bo.accounting.InterestRateBookingEvent;
import com.jore.epoc.bo.accounting.ProductsSoldBookingEvent;
import com.jore.epoc.bo.accounting.StorageCostBookingEvent;
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
    private String name;
    @ManyToOne(optional = false)
    private Simulation simulation;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "company", orphanRemoval = true)
    private CreditLine creditLine;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company", orphanRemoval = true)
    private List<UserInCompanyRole> users = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company", orphanRemoval = true)
    private List<Factory> factories = new ArrayList<>();
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

    public void addDistributionInMarket(DistributionInMarket distributionInMarket) {
        distributionInMarket.setCompany(this);
        distributionInMarkets.add(distributionInMarket);
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
        // TODO to be implemented using accounting
        log.debug("Booking of: " + bookingEvent);
    }

    public void chargeInterest(YearMonth simulationMonth) {
        if (creditLine != null) {
            InterestRateBookingEvent bookingEvent = new InterestRateBookingEvent();
            bookingEvent.setBookingText("Interest cost for month " + simulationMonth + " at rate " + creditLine.getInterestRate());
            bookingEvent.setBookingDate(simulationMonth.atDay(1));
            bookingEvent.setAmount(creditLine.getMonthlyInterest());
            book(bookingEvent);
        }
    }

    public void chargeStorageCost(YearMonth simulationMonth) {
        Optional<Money> storageCost = storages.stream().map(storage -> storage.getCost()).reduce((m1, m2) -> m1.add(m2));
        if (storageCost.isPresent()) {
            StorageCostBookingEvent bookingEvent = new StorageCostBookingEvent();
            bookingEvent.setBookingText("Storage cost for month " + simulationMonth);
            bookingEvent.setBookingDate(simulationMonth.atDay(1));
            bookingEvent.setAmount(storageCost.get());
            book(bookingEvent);
        }
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

    public int manufactureProducts(YearMonth productionMonth) {
        int totalAmountProduced = 0;
        int maximumToProduce = getStorages().stream().mapToInt(storage -> storage.getStoredRawMaterials()).sum();
        // TODO get max of storage or market capacity
        log.debug(String.format("Maximum to produce is %d for company '%s' (%d)", maximumToProduce, getName(), getId()));
        if (maximumToProduce > 0) {
            Iterator<Factory> iter = factories.iterator();
            while (iter.hasNext() && maximumToProduce > 0) {
                int amountProduced = iter.next().produce(maximumToProduce, productionMonth);
                maximumToProduce -= amountProduced;
                totalAmountProduced += amountProduced;
            }
            Storage.distributeProductAccrossStorages(storages, totalAmountProduced, productionMonth);
        }
        return totalAmountProduced;
    }

    public void sellMaximumOf(DistributionInMarket distributionInMarket, YearMonth simulationMonth, int productMarketPotential, Money sellPrice) {
        int storedAmount = storages.stream().mapToInt(storage -> storage.getStoredProducts()).sum();
        int intentedProductSale = distributionInMarket.getIntentedProductSale(simulationMonth);
        int maximumToSell = Math.min(Math.min(storedAmount, intentedProductSale), productMarketPotential);
        distributionInMarket.setSoldProducts(simulationMonth, maximumToSell);
        if (maximumToSell > 0) {
            ProductsSoldBookingEvent bookingEvent = new ProductsSoldBookingEvent();
            bookingEvent.setBookingText("Products sold for month " + simulationMonth);
            bookingEvent.setBookingDate(simulationMonth.atDay(1));
            bookingEvent.setAmount(sellPrice.multiply(maximumToSell));
            book(bookingEvent);
            Storage.takeProductsFromStorages(getStorages(), maximumToSell);
        }
        log.debug(String.format("Sell a maximum of %d products for month %s in %s. (Stored Amount: %d, Intented Product Sale: %d, Product Market Potential: %d", maximumToSell, simulationMonth, getName(), storedAmount, intentedProductSale, productMarketPotential));
    }
}
