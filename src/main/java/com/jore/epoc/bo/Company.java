package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.jore.Assert;
import com.jore.datatypes.currency.Currency;
import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.accounting.BookingRecord;
import com.jore.epoc.bo.accounting.DebitCreditAmount;
import com.jore.epoc.bo.accounting.FinancialAccounting;
import com.jore.epoc.bo.orders.AbstractSimulationOrder;
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
    public record MonthlySale(YearMonth simulationMonth, String name, Integer productsSold) {
    }

    private String name;
    @ManyToOne(optional = false)
    private Simulation simulation;
    @OneToOne(cascade = CascadeType.ALL, optional = true)
    private FinancialAccounting accounting;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company", orphanRemoval = true)
    private List<AbstractSimulationOrder> simulationOrders = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company", orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

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

    public void addMessage(Message message) {
        message.setCompany(this);
        messages.add(message);
    }

    public void addSimulationOrder(AbstractSimulationOrder simulationOrder) {
        Assert.notNull("Execution month in simulation order must not be null", simulationOrder.getExecutionMonth());
        simulationOrder.setCompany(this);
        simulationOrders.add(simulationOrder);
    }

    public void addStorage(Storage storage) {
        storage.setCompany(this);
        storages.add(storage);
    }

    public void book(BookingRecord bookingRecord) {
        accounting.book(bookingRecord);
    }

    public void chargeInterest(YearMonth simulationMonth) {
    }

    public void chargeStorageCost(YearMonth simulationMonth) {
        Optional<Money> storageCost = storages.stream().map(storage -> storage.getCost()).reduce((m1, m2) -> m1.add(m2));
        if (storageCost.isPresent()) {
        }
    }

    public void chargeWorkforceCost(YearMonth simulationMonth) {
        // TODO Auto-generated method stub
    }

    public boolean checkFunds(Money costsToBeCharged) {
        return accounting.checkFunds(costsToBeCharged);
    }

    public void depreciate(YearMonth simulationMonth) {
        // TODO Auto-generated method stub
    }

    public List<DistributionInMarket> getDistributionInMarkets() {
        return Collections.unmodifiableList(distributionInMarkets);
    }

    public List<Factory> getFactories() {
        return Collections.unmodifiableList(factories);
    }

    public List<AbstractSimulationOrder> getOrdersForExecutionIn(YearMonth simulationMonth) {
        return simulationOrders.stream().filter(order -> order.getExecutionMonth().equals(simulationMonth) && !order.isExecuted()).sorted(new Comparator<AbstractSimulationOrder>() {
            @Override
            public int compare(AbstractSimulationOrder o1, AbstractSimulationOrder o2) {
                return o1.getSortOrder() - o2.getSortOrder();
            }
        }).collect(Collectors.toList());
    }

    public Money getPnL() {
        return accounting.getPnL();
    }

    public List<AbstractSimulationOrder> getSimulationOrders() {
        return Collections.unmodifiableList(simulationOrders);
    }

    public Integer getSoldProducts() {
        return getDistributionInMarkets().stream().mapToInt(distribution -> distribution.getSoldProducts()).sum();
    }

    public List<MonthlySale> getSoldProductsPerMonth() {
        List<MonthlySale> result = new ArrayList<>();
        for (CompanySimulationStep companySimulationStep : companySimulationSteps) {
            result.addAll(companySimulationStep.getSoldProductsPerMonth());
        }
        return result;
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
            Storage.removeRawMaterialFromStorages(storages, totalAmountProduced);
            Storage.distributeProductAccrossStorages(storages, totalAmountProduced, productionMonth);
        }
        return totalAmountProduced;
    }

    public void sellMaximumOf(DistributionInMarket distributionInMarket, YearMonth simulationMonth, int productMarketPotential, Money sellPrice) {
        int storedAmount = storages.stream().mapToInt(storage -> storage.getStoredProducts()).sum();
        int intentedProductSale = distributionInMarket.getIntentedProductSale(simulationMonth);
        int maximumToSell = Math.min(Math.min(storedAmount, intentedProductSale), productMarketPotential);
        if (maximumToSell > 0) {
            distributionInMarket.setSoldProducts(simulationMonth, maximumToSell);
            Storage.removeProductsFromStorages(getStorages(), maximumToSell);
            // TODO book inventory decrease
            book(new BookingRecord(simulationMonth.atEndOfMonth(), "Sold %s products.", new DebitCreditAmount(FinancialAccounting.BANK, FinancialAccounting.PRODUKTE_ERLOESE, sellPrice.multiply(maximumToSell))));
        }
        log.debug(String.format("Sell a maximum of %d products for month %s in %s. (Stored Amount: %d, Intented Product Sale: %d, Product Market Potential: %d", maximumToSell, simulationMonth, getName(), storedAmount, intentedProductSale, productMarketPotential));
    }

    public void setBaseCurrency(Currency baseCurrency) {
        accounting.setBaseCurrency(baseCurrency);
    }

    public void setFinancialAccounting(FinancialAccounting accounting) {
        this.accounting = accounting;
    }
}
