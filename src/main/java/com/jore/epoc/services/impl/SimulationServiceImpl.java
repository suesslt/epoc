package com.jore.epoc.services.impl;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    public void buildFactory(Integer companySimulationId, FactoryOrderDto factoryOrderDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationId).get();
        Factory factory = new Factory();
        factory.setProductionLines(factoryOrderDto.getProductionLines());
        factory.setProductionStartMonth(companySimulationStep.getSimulationStep().getSimulationMonth().plusMonths(FACTORY_CREATION_MONTHS));
        Company company = companySimulationStep.getCompany();
        company.addFactory(factory);
    }

    @Override
    @Transactional
    public void buildStorage(Integer companySimulationId, StorageDto storageDto) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationId).get();
        Storage storage = new Storage();
        storage.setCapacity(storageDto.getCapacity());
        storage.setStorageStartMonth(companySimulationStep.getSimulationStep().getSimulationMonth().plusMonths(STORAGE_CREATION_MONTHS));
        Company company = companySimulationStep.getCompany();
        company.addStorage(storage);
    }

    @Override
    @Transactional
    public void buySimulations(String userLogin, int nrOfServices) {
        Optional<Login> user = loginRepository.findByLogin(userLogin);
        if (!user.isPresent()) {
            throw new IllegalStateException("User not present");
        }
        for (int i = 0; i < nrOfServices; i++) {
            Simulation simulation = new Simulation();
            simulation.setUser(user.get());
            simulation.setStarted(false);
            simulation.setStartMonth(START_MONTH);
            simulationRepository.save(simulation);
        }
    }

    @Override
    @Transactional
    public Integer countAvailableSimulations(String user) {
        return (int) simulationRepository.findByIsStartedAndUserLogin(false, user).size();
    }

    @Override
    @Transactional
    public void finishMoveFor(Integer companySimulationId) {
        CompanySimulationStep companySimulationStep = companySimulationStepRepository.findById(companySimulationId).get();
        companySimulationStep.setOpen(false);
        if (companySimulationStep.getSimulationStep().areAllCompanyStepsFinished()) {
            companySimulationStep.getSimulationStep().getSimulation().runSimulationStep();
        }
    }

    @Override
    @Transactional
    public CompanySimulationStepDto getCurrentCompanySimulationStep(Integer companyId) {
        CompanySimulationStepDto result = new CompanySimulationStepDto();
        Company company = companyRepository.findById(companyId).get();
        SimulationStep simulationStep = company.getSimulation().getCurrentSimulationStep();
        CompanySimulationStep companySimulationStep = company.getCompanySimulationStep(simulationStep);
        simulationStepRepository.save(simulationStep); // TODO Check if persistence can be made transitory in Company or even Simulation
        companySimulationStepRepository.save(companySimulationStep);
        result.setCompanyName(company.getName());
        result.setId(companySimulationStep.getId());
        for (Factory factory : company.getFactories()) {
            FactoryDto factoryDto = new FactoryDto();
            factoryDto.setId(factory.getId());
            result.addFactory(factoryDto);
        }
        for (CreditLine creditLine : company.getCreditLines()) {
            CreditLineDto creditLineDto = new CreditLineDto();
            creditLineDto.setId(creditLine.getId());
            result.addCreditLine(creditLineDto);
        }
        for (Storage storage : company.getStorages()) {
            StorageDto storageDto = StorageDto.builder().build();
            storageDto.setId(storage.getId());
            result.addStorage(storageDto);
        }
        for (DistributionInMarket distributionInMarket : company.getDistributionInMarkets()) {
            DistributionInMarketDto distributionInMarketDto = new DistributionInMarketDto();
            distributionInMarketDto.setId(distributionInMarket.getId());
            result.addDistributionInMarket(distributionInMarketDto);
        }
        return result;
    }

    @Override
    @Transactional
    public SimulationDto getNextAvailableSimulationForUser(String user) {
        SimulationDto result = null;
        Optional<Simulation> findFirst = simulationRepository.findByIsStartedAndUserLogin(false, user).stream().findFirst();
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
            Company company = userInCompany.getCompany();
            Simulation simulation = company.getSimulation();
            OpenUserSimulationDto openUserSimulationDto = new OpenUserSimulationDto();
            openUserSimulationDto.setSimulationId(simulation.getId());
            openUserSimulationDto.setSimulationName(simulation.getName());
            openUserSimulationDto.setCompanyName(company.getName());
            openUserSimulationDto.setCompanyId(company.getId());
            result.add(openUserSimulationDto);
        }
        return result;
    }

    @Override
    @Transactional
    public void updateSimulation(SimulationDto simulationDto) {
        Simulation simulation = simulationRepository.findById(simulationDto.getId()).get();
        simulation.setName(simulationDto.getName());
        simulation.setStartMonth(simulationDto.getStartMonth());
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
        simulationRepository.save(simulation);
    }
}
