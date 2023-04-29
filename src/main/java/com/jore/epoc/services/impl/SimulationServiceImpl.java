package com.jore.epoc.services.impl;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jore.datatypes.currency.Currency;
import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.CompanySimulationStep;
import com.jore.epoc.bo.CreditEventDirection;
import com.jore.epoc.bo.DistributionInMarket;
import com.jore.epoc.bo.EpocSetting;
import com.jore.epoc.bo.Factory;
import com.jore.epoc.bo.Login;
import com.jore.epoc.bo.Market;
import com.jore.epoc.bo.MarketSimulation;
import com.jore.epoc.bo.Simulation;
import com.jore.epoc.bo.SimulationStep;
import com.jore.epoc.bo.Storage;
import com.jore.epoc.bo.UserInCompanyRole;
import com.jore.epoc.bo.accounting.FinancialAccounting;
import com.jore.epoc.bo.orders.AdjustCreditLineOrder;
import com.jore.epoc.bo.orders.BuildFactoryOrder;
import com.jore.epoc.bo.orders.BuildStorageOrder;
import com.jore.epoc.bo.orders.BuyRawMaterialOrder;
import com.jore.epoc.bo.orders.ChangeAmountAndPriceOrder;
import com.jore.epoc.bo.orders.EnterMarketOrder;
import com.jore.epoc.dto.AdjustCreditLineDto;
import com.jore.epoc.dto.BuildFactoryDto;
import com.jore.epoc.dto.BuildStorageDto;
import com.jore.epoc.dto.BuyRawMaterialDto;
import com.jore.epoc.dto.CompanyDto;
import com.jore.epoc.dto.CompanySimulationStepDto;
import com.jore.epoc.dto.CompletedUserSimulationDto;
import com.jore.epoc.dto.DistributionInMarketDto;
import com.jore.epoc.dto.EnterMarketDto;
import com.jore.epoc.dto.FactoryDto;
import com.jore.epoc.dto.LoginDto;
import com.jore.epoc.dto.MarketDto;
import com.jore.epoc.dto.OpenUserSimulationDto;
import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.dto.SimulationStatisticsDto;
import com.jore.epoc.dto.StorageDto;
import com.jore.epoc.mapper.SimulationMapper;
import com.jore.epoc.repositories.CompanyRepository;
import com.jore.epoc.repositories.CompanySimulationStepRepository;
import com.jore.epoc.repositories.LoginRepository;
import com.jore.epoc.repositories.MarketRepository;
import com.jore.epoc.repositories.MarketSimulationRepository;
import com.jore.epoc.repositories.SimulationRepository;
import com.jore.epoc.repositories.SimulationStepRepository;
import com.jore.epoc.services.SimulationService;
import com.jore.epoc.services.StaticDataService;
import com.jore.util.Util;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class SimulationServiceImpl implements SimulationService {
    @Autowired
    private SimulationRepository simulationRepository;
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CompanySimulationStepRepository companySimulationStepRepository;
    @Autowired
    private SimulationStepRepository simulationStepRepository;
    @Autowired
    private MarketRepository marketRepository;
    @Autowired
    private StaticDataService staticDataService;
    @Autowired
    private MarketSimulationRepository marketSimulationRepository;

    @Override
    @Transactional
    public void buildFactory(Integer companySimulationStepId, BuildFactoryDto buildFactoryDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        BuildFactoryOrder buildFactoryOrder = new BuildFactoryOrder();
        buildFactoryOrder.setExecutionMonth(buildFactoryDto.getExecutionMonth());
        buildFactoryOrder.setProductionLines(buildFactoryDto.getProductionLines());
        buildFactoryOrder.setTimeToBuild((Integer) staticDataService.getSetting(EpocSetting.FACTORY_CREATION_MONTHS));
        buildFactoryOrder.setConstructionCosts((Money) staticDataService.getSetting(EpocSetting.FACTORY_FIXED_COSTS));
        buildFactoryOrder.setConstructionCostsPerLine((Money) staticDataService.getSetting(EpocSetting.FACTORY_VARIABLE_COSTS));
        buildFactoryOrder.setMonthlyCapacityPerProductionLine((Integer) staticDataService.getSetting(EpocSetting.MONTHLY_CAPACITY_PER_PRODUCTION_LINE));
        buildFactoryOrder.setUnitProductionCost((Money) staticDataService.getSetting(EpocSetting.UNIT_PRODUCTION_COST));
        buildFactoryOrder.setUnitLaborCost((Money) staticDataService.getSetting(EpocSetting.UNIT_LABOUR_COST));
        companySimulationStep.getCompany().addSimulationOrder(buildFactoryOrder);
    }

    @Override
    @Transactional
    public void buildStorage(Integer companySimulationStepId, BuildStorageDto buildStorageDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        BuildStorageOrder buildStorageOrder = new BuildStorageOrder();
        buildStorageOrder.setExecutionMonth(buildStorageDto.getExecutionMonth());
        buildStorageOrder.setCapacity(buildStorageDto.getCapacity());
        buildStorageOrder.setTimeToBuild((Integer) staticDataService.getSetting(EpocSetting.STORAGE_CREATION_MONTHS));
        buildStorageOrder.setConstructionCosts((Money) staticDataService.getSetting(EpocSetting.STORAGE_FIXED_COSTS));
        buildStorageOrder.setConstructionCostsPerUnit((Money) staticDataService.getSetting(EpocSetting.STORAGE_VARIABLE_COSTS));
        companySimulationStep.getCompany().addSimulationOrder(buildStorageOrder);
    }

    @Override
    @Transactional
    public void buyRawMaterial(Integer companySimulationStepId, BuyRawMaterialDto buyRawMaterialDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        BuyRawMaterialOrder buyRawMaterialOrder = new BuyRawMaterialOrder();
        buyRawMaterialOrder.setExecutionMonth(buyRawMaterialDto.getExecutionMonth());
        buyRawMaterialOrder.setAmount(buyRawMaterialDto.getAmount());
        buyRawMaterialOrder.setUnitPrice((Money) staticDataService.getSetting(EpocSetting.RAW_MATERIAL_UNIT_PRICE));
        companySimulationStep.getCompany().addSimulationOrder(buyRawMaterialOrder);
    }

    @Override
    @Transactional
    public void buySimulations(String userLogin, int nrOfSimulations) {
        Optional<Login> user = loginRepository.findByLogin(userLogin);
        if (!user.isPresent()) {
            throw new IllegalStateException("User not found: " + userLogin);
        }
        for (int i = 0; i < nrOfSimulations; i++) {
            Simulation simulation = new Simulation();
            simulation.setOwner(user.get());
            simulation.setIsStarted(false);
            simulation.setStartMonth((YearMonth) staticDataService.getSetting(EpocSetting.START_MONTH));
            simulation.setInterestRate((Percent) staticDataService.getSetting(EpocSetting.CREDIT_LINE_INTEREST_RATE));
            simulation.setBuildingMaintenanceCost((Money) staticDataService.getSetting(EpocSetting.BUILDING_MAINTENANCE));
            simulation.setDepreciationRate((Percent) staticDataService.getSetting(EpocSetting.DEPRECIATION_RATE));
            simulationRepository.save(simulation);
        }
    }

    @Override
    @Transactional
    public Integer countAvailableSimulations(String user) {
        return (int) simulationRepository.findByIsStartedAndOwnerLogin(false, user).size();
    }

    @Override
    public void decreaseCreditLine(Integer companySimulationStepId, AdjustCreditLineDto decreaseCreditLineDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        AdjustCreditLineOrder adjustCreditLineOrder = new AdjustCreditLineOrder();
        adjustCreditLineOrder.setExecutionMonth(decreaseCreditLineDto.getExecutionMonth());
        adjustCreditLineOrder.setDirection(CreditEventDirection.DECREASE);
        adjustCreditLineOrder.setAmount(decreaseCreditLineDto.getAmount());
        adjustCreditLineOrder.setInterestRate((Percent) staticDataService.getSetting(EpocSetting.CREDIT_LINE_INTEREST_RATE));
        companySimulationStep.getCompany().addSimulationOrder(adjustCreditLineOrder);
    }

    @Override
    @Transactional
    public void enterMarket(Integer companySimulationStepId, EnterMarketDto enterMarketDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        EnterMarketOrder enterMarketOrder = new EnterMarketOrder();
        Market market = marketRepository.findById(enterMarketDto.getMarketId()).get();
        Simulation simulation = companySimulationStep.getCompany().getSimulation();
        Optional<MarketSimulation> marketSimulation = marketSimulationRepository.findByMarketAndSimulation(market, simulation);
        if (marketSimulation.isEmpty()) {
            MarketSimulation thisMarketSimulation = new MarketSimulation();
            thisMarketSimulation.setMarket(market);
            thisMarketSimulation.setStartMonth(companySimulationStep.getSimulationStep().getSimulationMonth());
            thisMarketSimulation.setHigherPercent((Percent) staticDataService.getSetting(EpocSetting.DEMAND_HIGHER_PERCENT));
            thisMarketSimulation.setHigherPrice((Money) staticDataService.getSetting(EpocSetting.DEMAND_HIGHER_PRICE));
            thisMarketSimulation.setLowerPercent((Percent) staticDataService.getSetting(EpocSetting.DEMAND_LOWER_PERCENT));
            thisMarketSimulation.setLowerPrice((Money) staticDataService.getSetting(EpocSetting.DEMAND_LOWER_PRICE));
            thisMarketSimulation.setProductLifecycleDuration((Integer) staticDataService.getSetting(EpocSetting.PRODUCT_LIFECYCLE_DURATION));
            simulation.addMarketSimulation(thisMarketSimulation);
            marketSimulation = Optional.of(thisMarketSimulation);
        }
        enterMarketOrder.setExecutionMonth(enterMarketDto.getExecutionMonth());
        enterMarketOrder.setMarketSimulation(marketSimulation.get());
        enterMarketOrder.setIntentedProductSale(enterMarketDto.getIntentedProductSales());
        enterMarketOrder.setOfferedPrice(enterMarketDto.getOfferedPrice());
        enterMarketOrder.setFixedCosts((Money) staticDataService.getSetting(EpocSetting.DISTRIBUTION_FIXED_COSTS));
        companySimulationStep.getCompany().addSimulationOrder(enterMarketOrder);
    }

    @Override
    @Transactional
    public void finishMoveFor(Integer companySimulationStepId) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        companySimulationStep.getSimulationStep().getSimulation().finishCompanyStep(companySimulationStep);
    }

    @Override
    @Transactional
    public List<CompletedUserSimulationDto> getCompletedSimulationsForUser(String user) {
        List<CompletedUserSimulationDto> result = new ArrayList<>();
        Login login = loginRepository.findByLogin(user).get();
        for (UserInCompanyRole userInCompany : login.getCompanies()) {
            Company company = userInCompany.getCompany();
            Simulation simulation = company.getSimulation();
            if (simulation.isFinished()) {
                CompletedUserSimulationDto completedUserSimulationDto = new CompletedUserSimulationDto();
                completedUserSimulationDto.setSimulationId(simulation.getId());
                completedUserSimulationDto.setSimulationName(simulation.getName());
                completedUserSimulationDto.setCompanyName(company.getName());
                completedUserSimulationDto.setCompanyId(company.getId());
                result.add(completedUserSimulationDto);
            }
        }
        return result.stream().sorted(new Comparator<CompletedUserSimulationDto>() {
            @Override
            public int compare(CompletedUserSimulationDto o1, CompletedUserSimulationDto o2) {
                return o1.getSimulationName().compareTo(o2.getSimulationName());
            }
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<CompanySimulationStepDto> getCurrentCompanySimulationStep(Integer companyId) {
        Optional<CompanySimulationStepDto> result = Optional.empty();
        Company company = companyRepository.findById(companyId).get();
        Simulation simulation = company.getSimulation();
        Optional<SimulationStep> activeSimulationStep = simulation.getActiveSimulationStep();
        if (activeSimulationStep.isPresent()) {
            SimulationStep simulationStep = activeSimulationStep.get();
            simulationStepRepository.save(simulationStep);
            CompanySimulationStep companySimulationStep = simulationStep.getCompanySimulationStepFor(company);
            CompanySimulationStepDto companySimulationStepDto = new CompanySimulationStepDto();
            companySimulationStepDto.setCompanyName(company.getName());
            companySimulationStepDto.setId(companySimulationStep.getId());
            companySimulationStepDto.setSimulationMonth(simulationStep.getSimulationMonth());
            for (Factory factory : company.getFactories()) {
                FactoryDto factoryDto = new FactoryDto();
                factoryDto.setId(factory.getId());
                companySimulationStepDto.addFactory(factoryDto);
            }
            for (Storage storage : company.getStorages()) {
                StorageDto storageDto = StorageDto.builder().build();
                storageDto.setId(storage.getId());
                companySimulationStepDto.addStorage(storageDto);
            }
            for (DistributionInMarket distributionInMarket : company.getDistributionInMarkets()) {
                DistributionInMarketDto distributionInMarketDto = new DistributionInMarketDto();
                distributionInMarketDto.setId(distributionInMarket.getId());
                companySimulationStepDto.addDistributionInMarket(distributionInMarketDto);
            }
            for (Market market : marketRepository.findAll()) {
                MarketDto marketDto = new MarketDto();
                marketDto.setId(market.getId());
                companySimulationStepDto.addMarket(marketDto);
            }
            result = Optional.of(companySimulationStepDto);
        }
        return result;
    }

    @Override
    @Transactional
    public Optional<SimulationDto> getNextAvailableSimulationForOwner(String owner) {
        Optional<SimulationDto> result = Optional.empty();
        Optional<Simulation> simulation = simulationRepository.findByIsStartedAndOwnerLogin(false, owner).stream().findFirst();
        if (simulation.isPresent()) {
            result = Optional.of(SimulationMapper.INSTANCE.simulationToSimulationDto(simulation.get()));
        }
        return result;
    }

    @Override
    @Transactional
    public List<OpenUserSimulationDto> getOpenSimulationsForUser(String user) {
        List<OpenUserSimulationDto> result = new ArrayList<>();
        Login login = loginRepository.findByLogin(user).get();
        for (UserInCompanyRole userInCompany : login.getCompanies()) {
            Company company = userInCompany.getCompany();
            Simulation simulation = company.getSimulation();
            if (!simulation.isFinished()) {
                OpenUserSimulationDto openUserSimulationDto = new OpenUserSimulationDto();
                openUserSimulationDto.setSimulationId(simulation.getId());
                openUserSimulationDto.setSimulationName(simulation.getName());
                openUserSimulationDto.setCompanyName(company.getName());
                openUserSimulationDto.setCompanyId(company.getId());
                result.add(openUserSimulationDto);
            }
        }
        return result.stream().sorted(new Comparator<OpenUserSimulationDto>() {
            @Override
            public int compare(OpenUserSimulationDto o1, OpenUserSimulationDto o2) {
                return o1.getSimulationName().compareTo(o2.getSimulationName());
            }
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SimulationStatisticsDto getSimulationStatistics(Integer simulationId) {
        SimulationStatisticsDto result = new SimulationStatisticsDto();
        Simulation simulation = simulationRepository.findById(simulationId).get();
        result.setTotalSoldProducts(simulation.getSoldProducts());
        return result;
    }

    @Override
    @Transactional
    public void increaseCreditLine(Integer companySimulationStepId, AdjustCreditLineDto increaseCreditLineDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        AdjustCreditLineOrder adjustCreditLineOrder = new AdjustCreditLineOrder();
        adjustCreditLineOrder.setExecutionMonth(increaseCreditLineDto.getExecutionMonth());
        adjustCreditLineOrder.setDirection(CreditEventDirection.INCREASE);
        adjustCreditLineOrder.setAmount(increaseCreditLineDto.getAmount());
        adjustCreditLineOrder.setInterestRate((Percent) staticDataService.getSetting(EpocSetting.CREDIT_LINE_INTEREST_RATE));
        companySimulationStep.getCompany().addSimulationOrder(adjustCreditLineOrder);
    }

    @Override
    @Transactional
    public void setIntentedSalesAndPrice(Integer companySimulationStepId, Integer marketId, Integer intentedSales, Money price, YearMonth executionMonth) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        ChangeAmountAndPriceOrder changeIntentedAmountAndPriceOrder = new ChangeAmountAndPriceOrder();
        changeIntentedAmountAndPriceOrder.setExecutionMonth(executionMonth);
        changeIntentedAmountAndPriceOrder.setIntentedSales(intentedSales);
        changeIntentedAmountAndPriceOrder.setOfferedPrice(price);
        changeIntentedAmountAndPriceOrder.setMarket(marketRepository.findById(marketId).get());
        companySimulationStep.getCompany().addSimulationOrder(changeIntentedAmountAndPriceOrder);
    }

    @Override
    @Transactional
    public void updateSimulation(SimulationDto simulationDto) {
        Simulation simulation = simulationRepository.findById(simulationDto.getId()).get();
        if (!simulation.isStarted()) {
            simulation.setName(simulationDto.getName());
            simulation.setStartMonth(simulationDto.getStartMonth());
            simulation.setNrOfSteps(simulationDto.getNrOfSteps());
            for (CompanyDto companyDto : simulationDto.getCompanies()) {
                Company company = new Company();
                company.setId(companyDto.getId());
                company.setName(companyDto.getName());
                company.setFinancialAccounting(new FinancialAccounting());
                company.setBaseCurrency((Currency) staticDataService.getSetting(EpocSetting.BASE_CURRENCY));
                simulation.addCompany(company);
                companyRepository.save(company);
                for (LoginDto loginDto : companyDto.getUsers()) {
                    Optional<Login> login = loginRepository.findByLogin(loginDto.getEmail());
                    if (login.isEmpty()) {
                        login = Optional.of(new Login());
                        login.get().setAdmin(false);
                        login.get().setEmail(loginDto.getEmail());
                        login.get().setName(loginDto.getName());
                        login.get().setLogin(loginDto.getEmail());
                        login.get().setPassword(Util.createPassword((Integer) staticDataService.getSetting(EpocSetting.PASSWORD_LENGTH)));
                    }
                    UserInCompanyRole userInCompany = company.addLogin(login.get());
                    userInCompany.setIsInvitationRequired(true);
                    loginRepository.save(login.get());
                }
            }
        } else {
            log.warn(String.format("Tried to update simulation (%d) which is started.", simulation.getId()));
        }
    }
}
