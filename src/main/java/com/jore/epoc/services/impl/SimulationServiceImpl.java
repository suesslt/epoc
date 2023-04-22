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

import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.CompanySimulationStep;
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
import com.jore.epoc.bo.events.AdjustCreditLineEvent;
import com.jore.epoc.bo.events.BuildFactoryEvent;
import com.jore.epoc.bo.events.BuildStorageEvent;
import com.jore.epoc.bo.events.BuyRawMaterialEvent;
import com.jore.epoc.bo.events.DistributeInMarketEvent;
import com.jore.epoc.dto.CompanyDto;
import com.jore.epoc.dto.CompanySimulationStepDto;
import com.jore.epoc.dto.CreditLineDto;
import com.jore.epoc.dto.DistributionInMarketDto;
import com.jore.epoc.dto.FactoryDto;
import com.jore.epoc.dto.FactoryOrderDto;
import com.jore.epoc.dto.LoginDto;
import com.jore.epoc.dto.MarketDto;
import com.jore.epoc.dto.OpenUserSimulationDto;
import com.jore.epoc.dto.RawMaterialDto;
import com.jore.epoc.dto.SimulationDto;
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
    public void adjustCreditLine(Integer companySimulationStepId, CreditLineDto creditLineDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        AdjustCreditLineEvent adjustCreditLineEvent = new AdjustCreditLineEvent();
        adjustCreditLineEvent.setDirection(creditLineDto.getDirection());
        adjustCreditLineEvent.setAdjustAmount(creditLineDto.getAmount());
        adjustCreditLineEvent.setInterestRate((Percent) staticDataService.getSetting(EpocSetting.CREDIT_LINE_INTEREST_RATE));
        companySimulationStep.addEvent(adjustCreditLineEvent);
    }

    @Override
    @Transactional
    public void buildFactory(Integer companySimulationStepId, FactoryOrderDto factoryOrderDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        BuildFactoryEvent buildFactoryEvent = new BuildFactoryEvent();
        buildFactoryEvent.setProductionLines(factoryOrderDto.getProductionLines());
        buildFactoryEvent.setProductionStartMonth(companySimulationStep.getSimulationStep().getSimulationMonth().plusMonths((Integer) staticDataService.getSetting(EpocSetting.FACTORY_CREATION_MONTHS)));
        buildFactoryEvent.setFixedCosts((Money) staticDataService.getSetting(EpocSetting.FACTORY_FIXED_COSTS));
        buildFactoryEvent.setVariableCosts((Money) staticDataService.getSetting(EpocSetting.FACTORY_VARIABLE_COSTS));
        buildFactoryEvent.setMonthlyCapacityPerProductionLine((Integer) staticDataService.getSetting(EpocSetting.MONTHLY_CAPACITY_PER_PRODUCTION_LINE));
        buildFactoryEvent.setUnitProductionCost((Money) staticDataService.getSetting(EpocSetting.UNIT_PRODUCTION_COST));
        buildFactoryEvent.setUnitLabourCost((Money) staticDataService.getSetting(EpocSetting.UNIT_LABOUR_COST));
        companySimulationStep.addEvent(buildFactoryEvent);
    }

    @Override
    @Transactional
    public void buildStorage(Integer companySimulationStepId, StorageDto storageDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        BuildStorageEvent buildStorageEvent = new BuildStorageEvent();
        buildStorageEvent.setCapacity(storageDto.getCapacity());
        buildStorageEvent.setStorageStartMonth(companySimulationStep.getSimulationStep().getSimulationMonth().plusMonths((Integer) staticDataService.getSetting(EpocSetting.STORAGE_CREATION_MONTHS)));
        buildStorageEvent.setFixedCosts((Money) staticDataService.getSetting(EpocSetting.STORAGE_FIXED_COSTS));
        buildStorageEvent.setVariableCosts((Money) staticDataService.getSetting(EpocSetting.STORAGE_VARIABLE_COSTS));
        buildStorageEvent.setStorageCostPerUnitAndMonth((Money) staticDataService.getSetting(EpocSetting.STORAGE_COST_PER_UNIT_AND_MONTH));
        companySimulationStep.addEvent(buildStorageEvent);
    }

    @Override
    @Transactional
    public void buyRawMaterials(Integer companySimulationStepId, RawMaterialDto rawMaterialDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        BuyRawMaterialEvent buyRawMaterialEvent = new BuyRawMaterialEvent();
        buyRawMaterialEvent.setAmount(rawMaterialDto.getAmount());
        buyRawMaterialEvent.setUnitPrice((Money) staticDataService.getSetting(EpocSetting.RAW_MATERIAL_UNIT_PRICE));
        companySimulationStep.addEvent(buyRawMaterialEvent);
    }

    @Override
    @Transactional
    public void buySimulations(String userLogin, int nrOfSimulations) {
        Optional<Login> user = loginRepository.findByLogin(userLogin);
        if (!user.isPresent()) {
            throw new IllegalStateException("User not found");
        }
        for (int i = 0; i < nrOfSimulations; i++) {
            Simulation simulation = new Simulation();
            simulation.setOwner(user.get());
            simulation.setStarted(false);
            simulation.setStartMonth((YearMonth) staticDataService.getSetting(EpocSetting.START_MONTH));
            simulationRepository.save(simulation);
        }
    }

    @Override
    @Transactional
    public Integer countAvailableSimulations(String user) {
        return (int) simulationRepository.findByIsStartedAndOwnerLogin(false, user).size();
    }

    @Override
    @Transactional
    public void distributeInMarket(Integer companySimulationStepId, MarketDto marketDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        DistributeInMarketEvent distributeInMarketEvent = new DistributeInMarketEvent();
        Market market = marketRepository.findById(marketDto.getId()).get();
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
        distributeInMarketEvent.setMarketSimulation(marketSimulation.get());
        distributeInMarketEvent.setIntentedProductSale(1000);
        distributeInMarketEvent.setOfferedPrice(Money.of("CHF", 20));
        companySimulationStep.addEvent(distributeInMarketEvent);
    }

    @Override
    @Transactional
    public void finishMoveFor(Integer companySimulationStepId) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        companySimulationStep.getSimulationStep().getSimulation().finishCompanyStep(companySimulationStep);
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
            for (Factory factory : company.getFactories()) {
                FactoryDto factoryDto = new FactoryDto();
                factoryDto.setId(factory.getId());
                companySimulationStepDto.addFactory(factoryDto);
            }
            if (company.getCreditLine() != null) {
                CreditLineDto creditLineDto = CreditLineDto.builder().build();
                creditLineDto.setId(company.getCreditLine().getId());
                creditLineDto.setAmount(company.getCreditLine().getCreditAmount());
                companySimulationStepDto.setCreditLine(creditLineDto);
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
                simulation.addCompany(company);
                companyRepository.save(company);
                for (LoginDto loginDto : companyDto.getUsers()) {
                    Login login = new Login();
                    login.setAdmin(false);
                    login.setEmail(loginDto.getEmail());
                    login.setName(loginDto.getName());
                    login.setLogin(loginDto.getEmail());
                    login.setPassword(Util.createPassword((Integer) staticDataService.getSetting(EpocSetting.PASSWORD_LENGTH)));
                    UserInCompanyRole userInCompany = company.addLogin(login);
                    userInCompany.setInvitationRequired(true);
                    loginRepository.save(login);
                }
            }
        } else {
            log.warn(String.format("Tried to update simulation (%d) which is started.", simulation.getId()));
        }
    }
}
