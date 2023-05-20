package com.jore.epoc.services.impl;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.DistributionInMarket;
import com.jore.epoc.bo.Factory;
import com.jore.epoc.bo.Market;
import com.jore.epoc.bo.MarketSimulation;
import com.jore.epoc.bo.Simulation;
import com.jore.epoc.bo.Storage;
import com.jore.epoc.bo.accounting.FinancialAccounting;
import com.jore.epoc.bo.message.Message;
import com.jore.epoc.bo.orders.AdjustCreditLineOrder;
import com.jore.epoc.bo.orders.BuildFactoryOrder;
import com.jore.epoc.bo.orders.BuildStorageOrder;
import com.jore.epoc.bo.orders.BuyRawMaterialOrder;
import com.jore.epoc.bo.orders.ChangeAmountAndPriceOrder;
import com.jore.epoc.bo.orders.CreditEventDirection;
import com.jore.epoc.bo.orders.EnterMarketOrder;
import com.jore.epoc.bo.orders.IncreaseProductivityOrder;
import com.jore.epoc.bo.orders.IncreaseQualityOrder;
import com.jore.epoc.bo.orders.MarketingCampaignOrder;
import com.jore.epoc.bo.settings.EpocSetting;
import com.jore.epoc.bo.settings.EpocSettings;
import com.jore.epoc.bo.step.CompanySimulationStep;
import com.jore.epoc.bo.step.SimulationStep;
import com.jore.epoc.bo.user.User;
import com.jore.epoc.bo.user.UserInCompanyRole;
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
import com.jore.epoc.dto.IncreaseProductivityDto;
import com.jore.epoc.dto.IncreaseQualityDto;
import com.jore.epoc.dto.IntendedSalesAndPriceDto;
import com.jore.epoc.dto.UserDto;
import com.jore.epoc.dto.MarketDto;
import com.jore.epoc.dto.MessageDto;
import com.jore.epoc.dto.OpenUserSimulationDto;
import com.jore.epoc.dto.RunMarketingCampaignDto;
import com.jore.epoc.dto.SettingDto;
import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.dto.SimulationStatisticsDto;
import com.jore.epoc.dto.StorageDto;
import com.jore.epoc.mapper.SimulationMapper;
import com.jore.epoc.repositories.CompanyRepository;
import com.jore.epoc.repositories.CompanySimulationStepRepository;
import com.jore.epoc.repositories.LoginRepository;
import com.jore.epoc.repositories.MarketRepository;
import com.jore.epoc.repositories.MarketSimulationRepository;
import com.jore.epoc.repositories.SettingsRepository;
import com.jore.epoc.repositories.SimulationRepository;
import com.jore.epoc.repositories.SimulationStepRepository;
import com.jore.epoc.services.SimulationService;
import com.jore.util.Util;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@Validated
@Service
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
    private MarketSimulationRepository marketSimulationRepository;
    @Autowired
    private SettingsRepository settingsRepository;

    @Override
    @Transactional
    public void buildFactory(BuildFactoryDto buildFactoryDto) {
        Company company = companyRepository.findById(buildFactoryDto.getCompanyId()).get();
        logicalValidation(company, buildFactoryDto.getExecutionMonth());
        BuildFactoryOrder buildFactoryOrder = new BuildFactoryOrder();
        buildFactoryOrder.setExecutionMonth(buildFactoryDto.getExecutionMonth());
        buildFactoryOrder.setProductionLines(buildFactoryDto.getProductionLines());
        buildFactoryOrder.setTimeToBuild(company.getSimulation().getSettings().getTimeToBuild());
        buildFactoryOrder.setConstructionCost(company.getSimulation().getSettings().getFactoryConstructionCost());
        buildFactoryOrder.setConstructionCostPerLine(company.getSimulation().getSettings().getFactoryConstructionCostsPerLine());
        buildFactoryOrder.setDailyCapacityPerProductionLine(company.getSimulation().getSettings().getDailyCapacityPerProductionLine());
        buildFactoryOrder.setProductionLineLaborCost(company.getSimulation().getSettings().getProductionLineLaborCost());
        company.addSimulationOrder(buildFactoryOrder);
    }

    @Override
    @Transactional
    public void buildStorage(BuildStorageDto buildStorageDto) {
        Company company = companyRepository.findById(buildStorageDto.getCompanyId()).get();
        logicalValidation(company, buildStorageDto.getExecutionMonth());
        BuildStorageOrder buildStorageOrder = new BuildStorageOrder();
        buildStorageOrder.setExecutionMonth(buildStorageDto.getExecutionMonth());
        buildStorageOrder.setCapacity(buildStorageDto.getCapacity());
        buildStorageOrder.setTimeToBuild(company.getSimulation().getSettings().getStorageConstructionMonths());
        buildStorageOrder.setConstructionCost(company.getSimulation().getSettings().getStorageFixedCost());
        buildStorageOrder.setConstructionCostPerUnit(company.getSimulation().getSettings().getStorageCostPerUnit());
        buildStorageOrder.setInventoryManagementCost(company.getSimulation().getSettings().getInventoryManagementCost());
        company.addSimulationOrder(buildStorageOrder);
    }

    @Override
    @Transactional
    public void buyRawMaterial(BuyRawMaterialDto buyRawMaterialDto) {
        Company company = companyRepository.findById(buyRawMaterialDto.getCompanyId()).get();
        logicalValidation(company, buyRawMaterialDto.getExecutionMonth());
        BuyRawMaterialOrder buyRawMaterialOrder = new BuyRawMaterialOrder();
        buyRawMaterialOrder.setExecutionMonth(buyRawMaterialDto.getExecutionMonth());
        buyRawMaterialOrder.setAmount(buyRawMaterialDto.getAmount());
        buyRawMaterialOrder.setUnitPrice(company.getSimulation().getSettings().getRawMaterialUnitPrice());
        company.addSimulationOrder(buyRawMaterialOrder);
    }

    @Override
    @Transactional
    public void buySimulations(int nrOfSimulations) {
        checkUserPermission();
        EpocSettings settings = settingsRepository.findByIsTemplate(true).get();
        for (int i = 0; i < nrOfSimulations; i++) {
            Simulation simulation = new Simulation();
            simulation.setSettings(settings);
            simulation.setOwner(getLoggedInUser());
            simulation.setIsStarted(false);
            simulation.setStartMonth(settings.getSimulationStartMonth());
            simulation.setInterestRate(settings.getDebtInterestRate());
            simulation.setBuildingMaintenanceCost(settings.getMaintentanceCostPerBuilding());
            simulation.setHeadquarterCost(settings.getHeadquarterCost());
            simulation.setDepreciationRate(settings.getDepreciationRate());
            simulation.setProductionCost(settings.getProductionCostPerProduct());
            simulationRepository.save(simulation);
        }
    }

    @Override
    @Transactional
    public void decreaseCreditLine(AdjustCreditLineDto decreaseCreditLineDto) {
        Company company = companyRepository.findById(decreaseCreditLineDto.getCompanyId()).get();
        logicalValidation(company, decreaseCreditLineDto.getExecutionMonth());
        AdjustCreditLineOrder adjustCreditLineOrder = new AdjustCreditLineOrder();
        adjustCreditLineOrder.setExecutionMonth(decreaseCreditLineDto.getExecutionMonth());
        adjustCreditLineOrder.setDirection(CreditEventDirection.DECREASE);
        adjustCreditLineOrder.setAmount(decreaseCreditLineDto.getAmount());
        adjustCreditLineOrder.setInterestRate(company.getSimulation().getSettings().getDebtInterestRate());
        company.addSimulationOrder(adjustCreditLineOrder);
    }

    @Override
    @Transactional
    public void enterMarket(EnterMarketDto enterMarketDto) {
        Company company = companyRepository.findById(enterMarketDto.getCompanyId()).get();
        logicalValidation(company, enterMarketDto.getExecutionMonth());
        EnterMarketOrder enterMarketOrder = new EnterMarketOrder();
        Market market = marketRepository.findById(enterMarketDto.getMarketId()).get();
        Simulation simulation = company.getSimulation();
        Optional<MarketSimulation> marketSimulation = marketSimulationRepository.findByMarketAndSimulation(market, simulation);
        if (marketSimulation.isEmpty()) {
            MarketSimulation thisMarketSimulation = new MarketSimulation();
            thisMarketSimulation.setMarket(market);
            thisMarketSimulation.setStartMonth(enterMarketDto.getExecutionMonth());
            thisMarketSimulation.setHigherPercent(company.getSimulation().getSettings().getDemandHigherPercent());
            thisMarketSimulation.setHigherPrice(company.getSimulation().getSettings().getDemandHigherPrice());
            thisMarketSimulation.setLowerPercent(company.getSimulation().getSettings().getDemandLowerPercent());
            thisMarketSimulation.setLowerPrice(company.getSimulation().getSettings().getDemandLowerPrice());
            thisMarketSimulation.setProductLifecycleDuration(company.getSimulation().getSettings().getProductLifecycleDuration());
            simulation.addMarketSimulation(thisMarketSimulation);
            marketSimulation = Optional.of(thisMarketSimulation);
        }
        enterMarketOrder.setExecutionMonth(enterMarketDto.getExecutionMonth());
        enterMarketOrder.setMarketSimulation(marketSimulation.get());
        enterMarketOrder.setIntentedProductSale(enterMarketDto.getIntentedProductSales());
        enterMarketOrder.setOfferedPrice(enterMarketDto.getOfferedPrice());
        enterMarketOrder.setEnterMarktCost(market.getCostToEnterMarket());
        company.addSimulationOrder(enterMarketOrder);
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
        User login = loginRepository.findByLogin(user).get();
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
            for (Message message : company.getMessages()) {
                MessageDto messageDto = new MessageDto();
                messageDto.setLevel(message.getLevel());
                messageDto.setMessage(message.getMessage());
                messageDto.setRelevantMonth(message.getRelevantMonth());
                companySimulationStepDto.addMessage(messageDto);
            }
            result = Optional.of(companySimulationStepDto);
        }
        return result;
    }

    @Override
    @Transactional
    public Optional<SimulationDto> getNextAvailableSimulationForOwner() {
        checkUserPermission();
        Optional<SimulationDto> result = Optional.empty();
        Optional<Simulation> simulation = simulationRepository.findByIsStartedAndOwnerLogin(false, getLoggedInUser().getLogin()).stream().findFirst();
        if (simulation.isPresent()) {
            result = Optional.of(SimulationMapper.INSTANCE.simulationToSimulationDto(simulation.get()));
        }
        return result;
    }

    @Override
    @Transactional
    public List<OpenUserSimulationDto> getOpenSimulationsForUser() {
        checkUserPermission();
        List<OpenUserSimulationDto> result = new ArrayList<>();
        User login = loginRepository.findByLogin(getLoggedInUser().getLogin()).get();
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
    public void increaseCreditLine(AdjustCreditLineDto increaseCreditLineDto) {
        Company company = companyRepository.findById(increaseCreditLineDto.getCompanyId()).get();
        logicalValidation(company, increaseCreditLineDto.getExecutionMonth());
        AdjustCreditLineOrder adjustCreditLineOrder = new AdjustCreditLineOrder();
        adjustCreditLineOrder.setExecutionMonth(increaseCreditLineDto.getExecutionMonth());
        adjustCreditLineOrder.setDirection(CreditEventDirection.INCREASE);
        adjustCreditLineOrder.setAmount(increaseCreditLineDto.getAmount());
        adjustCreditLineOrder.setInterestRate(company.getSimulation().getSettings().getDebtInterestRate());
        company.addSimulationOrder(adjustCreditLineOrder);
    }

    @Override
    @Transactional
    public void increaseProductivity(IncreaseProductivityDto increaseProductivityDto) {
        Company company = companyRepository.findById(increaseProductivityDto.getCompanyId()).get();
        logicalValidation(company, increaseProductivityDto.getExecutionMonth());
        IncreaseProductivityOrder increaseProductivityOrder = new IncreaseProductivityOrder();
        increaseProductivityOrder.setExecutionMonth(increaseProductivityDto.getExecutionMonth());
        increaseProductivityOrder.setAmount(increaseProductivityDto.getIncreaseProductivityAmount());
        company.addSimulationOrder(increaseProductivityOrder);
    }

    @Override
    @Transactional
    public void increaseQuality(IncreaseQualityDto increaseQualityDto) {
        Company company = companyRepository.findById(increaseQualityDto.getCompanyId()).get();
        logicalValidation(company, increaseQualityDto.getExecutionMonth());
        IncreaseQualityOrder increaseQualityOrder = new IncreaseQualityOrder();
        increaseQualityOrder.setExecutionMonth(increaseQualityDto.getExecutionMonth());
        increaseQualityOrder.setAmount(increaseQualityDto.getIncreaseQualityAmount());
        company.addSimulationOrder(increaseQualityOrder);
    }

    @Override
    @Transactional
    public void runMarketingCampaign(RunMarketingCampaignDto runMarketingCampaignDto) {
        Company company = companyRepository.findById(runMarketingCampaignDto.getCompanyId()).get();
        logicalValidation(company, runMarketingCampaignDto.getExecutionMonth());
        MarketingCampaignOrder marketingCampaignOrder = new MarketingCampaignOrder();
        marketingCampaignOrder.setExecutionMonth(runMarketingCampaignDto.getExecutionMonth());
        marketingCampaignOrder.setAmount(runMarketingCampaignDto.getCampaignAmount());
        company.addSimulationOrder(marketingCampaignOrder);
    }

    @Override
    @Transactional
    public void setIntentedSalesAndPrice(IntendedSalesAndPriceDto intendedSalesAndPriceDto) {
        Company company = companyRepository.findById(intendedSalesAndPriceDto.getCompanyId()).get();
        logicalValidation(company, intendedSalesAndPriceDto.getExecutionMonth());
        ChangeAmountAndPriceOrder changeIntentedAmountAndPriceOrder = new ChangeAmountAndPriceOrder();
        changeIntentedAmountAndPriceOrder.setExecutionMonth(intendedSalesAndPriceDto.getExecutionMonth());
        changeIntentedAmountAndPriceOrder.setIntentedSales(intendedSalesAndPriceDto.getIntentedSales());
        changeIntentedAmountAndPriceOrder.setOfferedPrice(intendedSalesAndPriceDto.getPrice());
        changeIntentedAmountAndPriceOrder.setMarket(marketRepository.findById(intendedSalesAndPriceDto.getMarketId()).get());
        company.addSimulationOrder(changeIntentedAmountAndPriceOrder);
    }

    @Override
    @Transactional
    public void updateSimulation(SimulationDto simulationDto) {
        Simulation simulation = simulationRepository.findById(simulationDto.getId()).get();
        if (!simulation.isStarted()) {
            simulation.setName(simulationDto.getName());
            simulation.setStartMonth(simulationDto.getStartMonth()); // TODO Consider usting setting as default
            simulation.setNrOfMonths(simulationDto.getNrOfMonths());
            for (CompanyDto companyDto : simulationDto.getCompanies()) {
                Company company = new Company();
                company.setId(companyDto.getId());
                company.setName(companyDto.getName());
                company.setFinancialAccounting(new FinancialAccounting());
                company.setBaseCurrency(simulation.getSettings().getBaseCurrency());
                simulation.addCompany(company);
                companyRepository.save(company);
                for (UserDto loginDto : companyDto.getUsers()) {
                    Optional<User> login = loginRepository.findByLogin(loginDto.getEmail());
                    if (login.isEmpty()) {
                        login = Optional.of(new User());
                        login.get().setAdmin(false);
                        login.get().setEmail(loginDto.getEmail());
                        login.get().setName(loginDto.getName());
                        login.get().setLogin(loginDto.getEmail());
                        login.get().setPassword(Util.createPassword(simulation.getSettings().getPasswordLength()));
                    }
                    UserInCompanyRole userInCompany = company.addLogin(login.get());
                    userInCompany.setIsInvitationRequired(true);
                    loginRepository.save(login.get());
                }
            }
            if (!simulationDto.getSettings().isEmpty()) {
                EpocSettings simulationSettings = simulation.getSettings();
                if (simulationSettings.isTemplate()) {
                    simulationSettings = simulationSettings.copyWithoutId();
                    simulationSettings.setTemplate(false);
                    simulation.setSettings(simulationSettings);
                }
                for (SettingDto settingDto : simulationDto.getSettings()) {
                    EpocSetting setting = simulationSettings.getSettingByKey(settingDto.getSettingKey());
                    setting.setValueText(settingDto.getValueText());
                }
                settingsRepository.save(simulationSettings);
            }
        } else {
            // TODO write test case
            log.warn(String.format("Tried to update simulation (%d) which is started.", simulation.getId()));
        }
    }

    // TODO must be removed, goal is a stateless service
    private void checkUserPermission() {
        if (UserManagementServiceImpl.loggedInUser == null) {
            throw new IllegalStateException("No user logged in");
        }
    }

    // TODO must be removed, goal is a stateless service
    private User getLoggedInUser() {
        return UserManagementServiceImpl.loggedInUser;
    }

    private void logicalValidation(Company company, YearMonth executionMonth) {
        if (executionMonth.isBefore(company.getSimulation().getStartMonth())) {
            throw new ConstraintViolationException(null); // TODO add constraint violation with message, etc.
        }
        if (executionMonth.isAfter(company.getSimulation().getStartMonth().plusMonths(company.getSimulation().getNrOfMonths()))) {
            throw new ConstraintViolationException(null);
        }
    }
}
