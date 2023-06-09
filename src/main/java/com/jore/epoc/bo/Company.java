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
import com.jore.epoc.bo.accounting.DebitCreditAmount;
import com.jore.epoc.bo.accounting.FinancialAccounting;
import com.jore.epoc.bo.message.Message;
import com.jore.epoc.bo.message.Messages;
import com.jore.epoc.bo.orders.AbstractSimulationOrder;
import com.jore.epoc.bo.step.CompanySimulationStep;
import com.jore.epoc.bo.user.User;
import com.jore.epoc.bo.user.UserInCompanyRole;
import com.jore.jpa.AbstractBusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Entity
public class Company extends AbstractBusinessObject {
    private static final int ONE_TWELFTH = 12;
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
    // Factors
    private double qualityFactor = 1.0d;
    private double marketingFactor = 1.0d;
    private double productivityFactor = 1.0d;

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

    public UserInCompanyRole addLogin(User login) {
        UserInCompanyRole result = new UserInCompanyRole();
        result.setCompany(this);
        result.setUser(login);
        login.addCompanyRole(result);
        users.add(result);
        return result;
    }

    public void addMessage(Message message) {
        message.setCompany(this);
        messages.add(message);
    }

    public void addSimulationOrder(AbstractSimulationOrder simulationOrder) {
        Assert.notNull("Execution month in simulation order must not be null", simulationOrder.getExecutionMonth()); //$NON-NLS-1$
        simulationOrder.setCompany(this);
        simulationOrders.add(simulationOrder);
    }

    public void addStorage(Storage storage) {
        storage.setCompany(this);
        storages.add(storage);
    }

    public void chargeBuildingMaintenanceCost(YearMonth simulationMonth) {
        int nrOfBuildings = 1; // Main Building
        nrOfBuildings += factories.size();
        nrOfBuildings += storages.size();
        Money buildingCosts = simulation.getBuildingMaintenanceCost().multiply(nrOfBuildings).divide(12);
        getAccounting().book(String.format(Messages.getMessage("Company.1"), nrOfBuildings), simulationMonth.atDay(1), simulationMonth.atDay(1), new DebitCreditAmount(FinancialAccounting.RAUMAUFWAND, FinancialAccounting.BANK, buildingCosts)); //$NON-NLS-1$
    }

    public void chargeDepreciation(YearMonth simulationMonth) {
        Money realEstateBalance = accounting.getRealEstateBalance(simulationMonth.atEndOfMonth());
        Money depreciation = realEstateBalance.multiply(getSimulation().getDepreciationRate()).divide(12);
        getAccounting().book(String.format(Messages.getMessage("Company.2"), simulation.getDepreciationRate(), realEstateBalance), simulationMonth.atDay(1), simulationMonth.atDay(1), new DebitCreditAmount(FinancialAccounting.DEPRECIATION, FinancialAccounting.REAL_ESTATE, depreciation)); //$NON-NLS-1$
    }

    public void chargeInterest(YearMonth simulationMonth) {
        Money interestAmount = accounting.getLongTermDebt(simulationMonth.atEndOfMonth()).negate().multiply(simulation.getInterestRate()).divide(12);
        getAccounting()
                       .book(String.format(Messages.getMessage("Company.3"), simulation.getInterestRate(), accounting.getLongTermDebt(simulationMonth.atDay(1))), simulationMonth.atDay(1), simulationMonth.atDay(1), //$NON-NLS-1$
                               new DebitCreditAmount(FinancialAccounting.INTEREST, FinancialAccounting.BANK, interestAmount));
    }

    public void chargeWorkforceCost(YearMonth simulationMonth) {
        Money headquarterCost = getSimulation().getHeadquarterCost();
        Optional<Money> distributionCost = getDistributionInMarkets().stream().map(dim -> dim.getDistributionCost()).reduce((c1, c2) -> c1.add(c2));
        Optional<Money> inventoryManagementCost = getStorages().stream().map(storage -> storage.getInventoryManagementCost()).reduce((c1, c2) -> c1.add(c2));
        Optional<Money> productionCost = getFactories().stream().map(factory -> factory.getProductionCost()).reduce((c1, c2) -> c1.add(c2));
        Money workforceCost = headquarterCost;
        workforceCost = Money.add(workforceCost, distributionCost.orElse(null));
        workforceCost = Money.add(workforceCost, inventoryManagementCost.orElse(null));
        workforceCost = Money.add(workforceCost, productionCost.orElse(null));
        workforceCost = workforceCost.divide(ONE_TWELFTH);
        getAccounting()
                       .book(String.format(Messages.getMessage("Company.4"), headquarterCost, distributionCost, inventoryManagementCost, productionCost), simulationMonth.atDay(1), simulationMonth.atDay(1), //$NON-NLS-1$
                               new DebitCreditAmount(FinancialAccounting.SALARIES, FinancialAccounting.BANK, workforceCost));
    }

    public void discountFactors() {
        qualityFactor = new PercentDiscountFactor().setDiscountRate(getSimulation().getSettings().getFactorDiscountRate()).discount(qualityFactor);
        marketingFactor = new PercentDiscountFactor().setDiscountRate(getSimulation().getSettings().getFactorDiscountRate()).discount(marketingFactor);
        productivityFactor = new PercentDiscountFactor().setDiscountRate(getSimulation().getSettings().getFactorDiscountRate()).discount(productivityFactor);
    }

    public FinancialAccounting getAccounting() {
        return accounting;
    }

    public List<CompanySimulationStep> getCompanySimulationSteps() {
        return Collections.unmodifiableList(companySimulationSteps);
    }

    public List<DistributionInMarket> getDistributionInMarkets() {
        return Collections.unmodifiableList(distributionInMarkets);
    }

    public List<Factory> getFactories() {
        return Collections.unmodifiableList(factories);
    }

    public double getMarketingFactor() {
        return marketingFactor;
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public String getName() {
        return name;
    }

    public List<AbstractSimulationOrder> getOrdersForExecutionIn(YearMonth simulationMonth) {
        return simulationOrders.stream().filter(order -> order.getExecutionMonth().equals(simulationMonth) && !order.isExecuted()).sorted(new Comparator<AbstractSimulationOrder>() {
            @Override
            public int compare(AbstractSimulationOrder o1, AbstractSimulationOrder o2) {
                return o1.getSortOrder() - o2.getSortOrder();
            }
        }).collect(Collectors.toList());
    }

    public double getProductivityFactor() {
        return productivityFactor;
    }

    public double getQualityFactor() {
        return qualityFactor;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public Integer getSoldProducts() {
        return distributionInMarkets.stream().mapToInt(distribution -> distribution.getSoldProducts()).sum();
    }

    public List<Storage> getStorages() {
        return Collections.unmodifiableList(storages);
    }

    public List<UserInCompanyRole> getUsers() {
        return Collections.unmodifiableList(users);
    }

    public int manufactureProducts(YearMonth productionMonth) {
        int totalAmountProduced = 0;
        int rawMaterialInStorage = getStorages().stream().mapToInt(storage -> storage.getStoredRawMaterials()).sum();
        log.debug(Messages.getMessage("Company.5", rawMaterialInStorage, name, getId()));
        if (rawMaterialInStorage > 0) {
            Iterator<Factory> factoryIterator = factories.iterator();
            while (factoryIterator.hasNext() && rawMaterialInStorage > 0) {
                int amountProduced = factoryIterator.next().produce(rawMaterialInStorage, productionMonth, getProductivityFactor());
                rawMaterialInStorage -= amountProduced;
                totalAmountProduced += amountProduced;
            }
            Money averageRawMaterialPrice = Storage.getAverageRawMaterialPrice(storages);
            accounting
                      .book(Messages.getMessage("Company.6"), productionMonth.atEndOfMonth(), productionMonth.atEndOfMonth(), //$NON-NLS-1$
                              new DebitCreditAmount(FinancialAccounting.BESTANDESAENDERUNGEN_ROHWAREN, FinancialAccounting.RAW_MATERIALS, averageRawMaterialPrice.multiply(totalAmountProduced)));
            accounting
                      .book(Messages.getMessage("Company.7"), productionMonth.atEndOfMonth(), productionMonth.atEndOfMonth(), //$NON-NLS-1$
                              new DebitCreditAmount(FinancialAccounting.PRODUCTS, FinancialAccounting.BESTANDESAENDERUNGEN_PRODUKTE, getSimulation().getProductionCost().multiply(totalAmountProduced)));
            Storage.removeRawMaterialFromStorages(storages, totalAmountProduced);
            Storage.distributeProductAccrossStorages(storages, totalAmountProduced, productionMonth);
        }
        return totalAmountProduced;
    }

    public void sellMaximumOf(DistributionInMarket distributionInMarket, YearMonth simulationMonth, int productMarketPotential, Money sellPrice) {
        int storedAmount = storages.stream().mapToInt(storage -> storage.getStoredProducts()).sum();
        int intentedProductSale = distributionInMarket.getIntentedProductSale(simulationMonth);
        int amountToSell = Math.min(Math.min(storedAmount, intentedProductSale), productMarketPotential);
        if (amountToSell > 0) {
            distributionInMarket.setSoldProducts(simulationMonth, amountToSell);
            Storage.removeProductsFromStorages(getStorages(), amountToSell);
            accounting
                      .book(String.format(Messages.getMessage("Company.8"), amountToSell), simulationMonth.atEndOfMonth(), simulationMonth.atEndOfMonth(), new DebitCreditAmount(FinancialAccounting.BANK, FinancialAccounting.PRODUCT_REVENUES, sellPrice.multiply(amountToSell)), //$NON-NLS-1$
                              new DebitCreditAmount(FinancialAccounting.BESTANDESAENDERUNGEN_PRODUKTE, FinancialAccounting.PRODUCTS, getSimulation().getProductionCost().multiply(amountToSell)));
        }
        log.debug(String.format(Messages.getMessage("Company.9"), amountToSell, simulationMonth, name, storedAmount, intentedProductSale, productMarketPotential)); //$NON-NLS-1$
    }

    public void setAccounting(FinancialAccounting accounting) {
        this.accounting = accounting;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        accounting.setBaseCurrency(baseCurrency);
    }

    public void setFinancialAccounting(FinancialAccounting accounting) {
        this.accounting = accounting;
    }

    public void setMarketingFactor(double marketingFactor) {
        this.marketingFactor = marketingFactor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProductivityFactor(double productivityFactor) {
        this.productivityFactor = productivityFactor;
    }

    public void setQualityFactor(double qualityFactor) {
        this.qualityFactor = qualityFactor;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }
}
