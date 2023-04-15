package com.jore.epoc.services.impl;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.BuildFactoryEvent;
import com.jore.epoc.bo.BuildStorageEvent;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.CompanySimulationStep;
import com.jore.epoc.bo.CreditLine;
import com.jore.epoc.bo.DistributionInMarket;
import com.jore.epoc.bo.Factory;
import com.jore.epoc.bo.Login;
import com.jore.epoc.bo.Simulation;
import com.jore.epoc.bo.SimulationStep;
import com.jore.epoc.bo.Storage;
import com.jore.epoc.bo.UserInCompanyRole;
import com.jore.epoc.dto.CompanyDto;
import com.jore.epoc.dto.CompanySimulationStepDto;
import com.jore.epoc.dto.CreditLineDto;
import com.jore.epoc.dto.DistributionInMarketDto;
import com.jore.epoc.dto.FactoryDto;
import com.jore.epoc.dto.FactoryOrderDto;
import com.jore.epoc.dto.LoginDto;
import com.jore.epoc.dto.OpenUserSimulationDto;
import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.dto.StorageDto;
import com.jore.epoc.mapper.SimulationMapper;
import com.jore.epoc.repositories.CompanyRepository;
import com.jore.epoc.repositories.CompanySimulationStepRepository;
import com.jore.epoc.repositories.LoginRepository;
import com.jore.epoc.repositories.SimulationRepository;
import com.jore.epoc.repositories.SimulationStepRepository;
import com.jore.epoc.repositories.UserInCompanyRoleRepository;
import com.jore.epoc.services.SimulationService;
import com.jore.util.Util;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class SimulationServiceImpl implements SimulationService {
    private static final YearMonth START_MONTH = YearMonth.of(2000, 1);
    private static final long FACTORY_CREATION_MONTHS = 3;
    private static final long STORAGE_CREATION_MONTHS = 1;
    @Autowired
    private SimulationRepository simulationRepository;
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserInCompanyRoleRepository userInCompanyRoleRepository;
    @Autowired
    private CompanySimulationStepRepository companySimulationStepRepository;
    @Autowired
    private SimulationStepRepository simulationStepRepository;

    @Override
    @Transactional
    public void buildFactory(Integer companySimulationStepId, FactoryOrderDto factoryOrderDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationStepId).get();
        BuildFactoryEvent buildFactoryEvent = new BuildFactoryEvent();
        buildFactoryEvent.setProductionLines(factoryOrderDto.getProductionLines());
        buildFactoryEvent.setProductionStartMonth(companySimulationStep.getSimulationStep().getSimulationMonth().plusMonths(FACTORY_CREATION_MONTHS));
        buildFactoryEvent.setFixedCosts(Money.of("CHF", 1000000));
        buildFactoryEvent.setVariableCosts(Money.of("CHF", 100000));
        companySimulationStep.addEvent(buildFactoryEvent);
    }

    @Override
    @Transactional
    public void buildStorage(Integer companySimulationId, StorageDto storageDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationId).get();
        BuildStorageEvent buildStorageEvent = new BuildStorageEvent();
        buildStorageEvent.setCapacity(storageDto.getCapacity());
        buildStorageEvent.setStorageStartMonth(companySimulationStep.getSimulationStep().getSimulationMonth().plusMonths(STORAGE_CREATION_MONTHS));
        buildStorageEvent.setFixedCosts(Money.of("CHF", 1000000));
        buildStorageEvent.setVariableCosts(Money.of("CHF", 1000));
        companySimulationStep.addEvent(buildStorageEvent);
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
            simulation.setStartMonth(START_MONTH);
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
            for (CreditLine creditLine : company.getCreditLines()) {
                CreditLineDto creditLineDto = new CreditLineDto();
                creditLineDto.setId(creditLine.getId());
                companySimulationStepDto.addCreditLine(creditLineDto);
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
            result = Optional.of(companySimulationStepDto);
        }
        return result;
    }

    @Override
    @Transactional
    public SimulationDto getNextAvailableSimulationForOwner(String owner) {
        SimulationDto result = null;
        Optional<Simulation> findFirst = simulationRepository.findByIsStartedAndOwnerLogin(false, owner).stream().findFirst();
        if (findFirst.isPresent()) {
            result = SimulationMapper.INSTANCE.simulationToSimulationDto(findFirst.get());
        }
        return result;
    }

    @Override
    @Transactional
    public List<OpenUserSimulationDto> getOpenSimulationsForUser(String user) {
        List<OpenUserSimulationDto> result = new ArrayList<>();
        Login login = loginRepository.findByLogin(user).get();
        for (UserInCompanyRole userInCompany : login.getCompanies()) {
            // TODO Skip finished simulations?
            Company company = userInCompany.getCompany();
            Simulation simulation = company.getSimulation();
            OpenUserSimulationDto openUserSimulationDto = new OpenUserSimulationDto();
            openUserSimulationDto.setSimulationId(simulation.getId());
            openUserSimulationDto.setSimulationName(simulation.getName());
            openUserSimulationDto.setCompanyName(company.getName());
            openUserSimulationDto.setCompanyId(company.getId());
            result.add(openUserSimulationDto);
        }
        // TODO Ordered by what?
        return result;
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
                    login.setPassword(Util.createPassword(12));
                    UserInCompanyRole userInCompany = company.addLogin(login);
                    userInCompany.setInvitationRequired(true);
                    loginRepository.save(login);
                    userInCompanyRoleRepository.save(userInCompany);
                }
            }
        } else {
            log.warn(String.format("Tried to update simulation (%d) which is started.", simulation.getId()));
        }
    }
}
