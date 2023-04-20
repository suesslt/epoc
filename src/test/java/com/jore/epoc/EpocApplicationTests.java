package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.CreditEventDirection;
import com.jore.epoc.dto.CompanyDto;
import com.jore.epoc.dto.CompanySimulationStepDto;
import com.jore.epoc.dto.CreditLineDto;
import com.jore.epoc.dto.FactoryOrderDto;
import com.jore.epoc.dto.LoginDto;
import com.jore.epoc.dto.OpenUserSimulationDto;
import com.jore.epoc.dto.RawMaterialDto;
import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.dto.StorageDto;
import com.jore.epoc.services.SimulationService;
import com.jore.epoc.services.StaticDataService;
import com.jore.epoc.services.UserManagementService;
import com.jore.mail.service.SendMailService;
import com.jore.util.DatabaseViewer;

import jakarta.persistence.EntityManager;

@SpringBootTest
class EpocApplicationTests {
    private static final int NR_OF_SIM_STEPS = 3;
    private static final String MAX = "max.mara@bluewin.ch";
    private static final String RETO = "reto.straumann@bluewin.ch";
    private static final String FELIX = "felix.haeppy@bluewin.ch";
    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private SimulationService simulationService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private StaticDataService staticDataService;;
    private SendMailService sendMailService = new StubSendMailServiceImpl();

    @Test
    public void testUseCasesInOneRow() {
        userManagementService.createInitialUser("admin", "g00dPa&word");
        //
        // Create user for simulation and delete ad min
        //
        userManagementService.login("admin", "g00dPa&word");
        userManagementService.createAdmin(LoginDto.builder().login("epocadmin").name("Epoc").email("admin@epoc.ch").password("badpw").build());
        userManagementService.logout();
        userManagementService.login("epocadmin", "badpw");
        userManagementService.deleteLogin("admin");
        staticDataService.loadMarkets("markets.xlsx");
        staticDataService.loadSettings("EpocSettings.xlsx");
        userManagementService.logout(); // TODO Uh, this is not good...
        userManagementService.createUser(LoginDto.builder().login("user").name("Thomas").email("thomas.s@epoc.ch").password("e*Wasdf_erwer23").build());
        userManagementService.logout();
        //
        // Login as game user, buy simulations, create companies with users
        //
        userManagementService.login("user", "e*Wasdf_erwer23");
        simulationService.buySimulations("user", 1);
        Optional<SimulationDto> simulation = simulationService.getNextAvailableSimulationForOwner("user");
        simulation.get().setName("This is my first real simulation!");
        simulation.get().setStartMonth(YearMonth.of(2023, 1));
        simulation.get().setNrOfSteps(NR_OF_SIM_STEPS);
        simulation.get().addCompany(CompanyDto.builder().name("Company A").users(Arrays.asList(LoginDto.builder().email(MAX).build(), LoginDto.builder().email("kurt.gruen@bluewin.ch").build())).build());
        simulation.get().addCompany(CompanyDto.builder().name("Company B").users(Arrays.asList(LoginDto.builder().email(RETO).build())).build());
        simulation.get().addCompany(CompanyDto.builder().name("Company C").users(Arrays.asList(LoginDto.builder().email(FELIX).build(), LoginDto.builder().email("peter.gross@bluewin.ch").build(), LoginDto.builder().email("beat-huerg.minder@bluewin.ch").build())).build());
        simulationService.updateSimulation(simulation.get());
        sendMailService.send(userManagementService.getEmailsForNewUsers());
        userManagementService.logout();
        //
        // Step 1 for Company A
        //
        userManagementService.login(MAX, ((StubSendMailServiceImpl) sendMailService).getPassword(MAX));
        List<OpenUserSimulationDto> simulations1A = simulationService.getOpenSimulationsForUser(MAX);
        Optional<CompanySimulationStepDto> companySimulationStep1A = simulationService.getCurrentCompanySimulationStep(simulations1A.get(0).getCompanyId());
        simulationService.adjustCreditLine(companySimulationStep1A.get().getId(), CreditLineDto.builder().direction(CreditEventDirection.INCREASE).amount(Money.of("CHF", 10000000)).build());
        simulationService.buildStorage(companySimulationStep1A.get().getId(), StorageDto.builder().capacity(1000).build());
        simulationService.buildFactory(companySimulationStep1A.get().getId(), FactoryOrderDto.builder().productionLines(5).build());
        simulationService.finishMoveFor(companySimulationStep1A.get().getId());
        userManagementService.logout();
        //
        // Step 1 for Company B
        //
        userManagementService.login(RETO, ((StubSendMailServiceImpl) sendMailService).getPassword(RETO));
        List<OpenUserSimulationDto> simulations1B = simulationService.getOpenSimulationsForUser(RETO);
        Optional<CompanySimulationStepDto> companySimulationStep1B = simulationService.getCurrentCompanySimulationStep(simulations1B.get(0).getCompanyId());
        simulationService.finishMoveFor(companySimulationStep1B.get().getId());
        userManagementService.logout();
        //
        // Step 1 for Company C
        //
        userManagementService.login(FELIX, ((StubSendMailServiceImpl) sendMailService).getPassword(FELIX));
        List<OpenUserSimulationDto> simulations1C = simulationService.getOpenSimulationsForUser(FELIX);
        Optional<CompanySimulationStepDto> companySimulationStep1C = simulationService.getCurrentCompanySimulationStep(simulations1C.get(0).getCompanyId());
        simulationService.finishMoveFor(companySimulationStep1C.get().getId());
        userManagementService.logout();
        //
        // Step 2 for Company A
        //
        userManagementService.login(MAX, ((StubSendMailServiceImpl) sendMailService).getPassword(MAX));
        List<OpenUserSimulationDto> simulations2A = simulationService.getOpenSimulationsForUser(MAX);
        Optional<CompanySimulationStepDto> companySimulationStep2A = simulationService.getCurrentCompanySimulationStep(simulations2A.get(0).getCompanyId());
        simulationService.buyRawMaterials(companySimulationStep2A.get().getId(), RawMaterialDto.builder().amount(10000).build());
        simulationService.finishMoveFor(companySimulationStep2A.get().getId());
        userManagementService.logout();
        //
        // Step 2 for Company B
        //
        userManagementService.login(RETO, ((StubSendMailServiceImpl) sendMailService).getPassword(RETO));
        List<OpenUserSimulationDto> simulations2B = simulationService.getOpenSimulationsForUser(RETO);
        Optional<CompanySimulationStepDto> companySimulationStep2B = simulationService.getCurrentCompanySimulationStep(simulations2B.get(0).getCompanyId());
        simulationService.finishMoveFor(companySimulationStep2B.get().getId());
        userManagementService.logout();
        //
        // Step 2 for Company C
        //
        userManagementService.login(FELIX, ((StubSendMailServiceImpl) sendMailService).getPassword(FELIX));
        List<OpenUserSimulationDto> simulations2C = simulationService.getOpenSimulationsForUser(FELIX);
        Optional<CompanySimulationStepDto> companySimulationStep2C = simulationService.getCurrentCompanySimulationStep(simulations2C.get(0).getCompanyId());
        simulationService.finishMoveFor(companySimulationStep2C.get().getId());
        userManagementService.logout();
        //
        // Step 3 for Company A
        //
        userManagementService.login(MAX, ((StubSendMailServiceImpl) sendMailService).getPassword(MAX));
        List<OpenUserSimulationDto> simulations3A = simulationService.getOpenSimulationsForUser(MAX);
        Optional<CompanySimulationStepDto> companySimulationStep3A = simulationService.getCurrentCompanySimulationStep(simulations3A.get(0).getCompanyId());
        simulationService.distributeInMarket(companySimulationStep3A.get().getId(), companySimulationStep3A.get().getMarkets().get(0));
        simulationService.finishMoveFor(companySimulationStep3A.get().getId());
        userManagementService.logout();
        //
        // Step 3 for Company B
        //
        userManagementService.login(RETO, ((StubSendMailServiceImpl) sendMailService).getPassword(RETO));
        List<OpenUserSimulationDto> simulations3B = simulationService.getOpenSimulationsForUser(RETO);
        Optional<CompanySimulationStepDto> companySimulationStep3B = simulationService.getCurrentCompanySimulationStep(simulations3B.get(0).getCompanyId());
        simulationService.finishMoveFor(companySimulationStep3B.get().getId());
        userManagementService.logout();
        //
        // Step 3 for Company C
        //
        userManagementService.login(FELIX, ((StubSendMailServiceImpl) sendMailService).getPassword(FELIX));
        List<OpenUserSimulationDto> simulations3C = simulationService.getOpenSimulationsForUser(FELIX);
        Optional<CompanySimulationStepDto> companySimulationStep3C = simulationService.getCurrentCompanySimulationStep(simulations3C.get(0).getCompanyId());
        simulationService.finishMoveFor(companySimulationStep3C.get().getId());
        userManagementService.logout();
        //
        // Step 4 for Company A
        //
        userManagementService.login(MAX, ((StubSendMailServiceImpl) sendMailService).getPassword(MAX));
        List<OpenUserSimulationDto> simulations4A = simulationService.getOpenSimulationsForUser(MAX); // TODO should be empty - or not?
        Optional<CompanySimulationStepDto> companySimulationStep4A = simulationService.getCurrentCompanySimulationStep(simulations4A.get(0).getCompanyId());
        assertTrue(companySimulationStep4A.isEmpty());
        //
        // Display Database
        //
        DatabaseViewer.logDatabase(entityManager);
    }
}
