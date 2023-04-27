package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.CreditEventDirection;
import com.jore.epoc.bo.DistributionInMarket;
import com.jore.epoc.bo.DistributionStep;
import com.jore.epoc.bo.Factory;
import com.jore.epoc.bo.Login;
import com.jore.epoc.bo.Simulation;
import com.jore.epoc.bo.SimulationStep;
import com.jore.epoc.bo.Storage;
import com.jore.epoc.dto.AdjustCreditLineDto;
import com.jore.epoc.dto.BuildFactoryDto;
import com.jore.epoc.dto.BuildStorageDto;
import com.jore.epoc.dto.BuyRawMaterialDto;
import com.jore.epoc.dto.CompanyDto;
import com.jore.epoc.dto.CompanySimulationStepDto;
import com.jore.epoc.dto.CompletedUserSimulationDto;
import com.jore.epoc.dto.EnterMarketDto;
import com.jore.epoc.dto.LoginDto;
import com.jore.epoc.dto.OpenUserSimulationDto;
import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.dto.SimulationStatisticsDto;
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
    public void testShortSimulationAndOpeningOfNew() {
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
        userManagementService.logout(); // TODO Uh, this is not good. But will fix definitely when implementing security...
        userManagementService.createUser(LoginDto.builder().login("user").name("Thomas").email("thomas.s@epoc.ch").password("e*Wasdf_erwer23").build());
        userManagementService.logout();
        //
        // Login as game user, buy simulations, create companies with users
        //
        {
            userManagementService.login("user", "e*Wasdf_erwer23");
            simulationService.buySimulations("user", 2);
            SimulationDto simulation = simulationService.getNextAvailableSimulationForOwner("user").get();
            simulation.setName("This is my first real simulation!");
            simulation.setStartMonth(YearMonth.of(2023, 1));
            simulation.setNrOfSteps(NR_OF_SIM_STEPS);
            simulation.addCompany(CompanyDto.builder().name("Company A").users(Arrays.asList(LoginDto.builder().email(MAX).build(), LoginDto.builder().email("kurt.gruen@bluewin.ch").build())).build());
            simulation.addCompany(CompanyDto.builder().name("Company B").users(Arrays.asList(LoginDto.builder().email(RETO).build())).build());
            simulation.addCompany(CompanyDto.builder().name("Company C").users(Arrays.asList(LoginDto.builder().email(FELIX).build(), LoginDto.builder().email("peter.gross@bluewin.ch").build(), LoginDto.builder().email("beat-huerg.minder@bluewin.ch").build())).build());
            simulationService.updateSimulation(simulation);
            sendMailService.send(userManagementService.getEmailsForNewUsers());
            userManagementService.logout();
        }
        //
        // Step 1 for Company A
        //
        userManagementService.login(MAX, ((StubSendMailServiceImpl) sendMailService).getPassword(MAX));
        List<OpenUserSimulationDto> simulations1A = simulationService.getOpenSimulationsForUser(MAX);
        Optional<CompanySimulationStepDto> companySimulationStep1A = simulationService.getCurrentCompanySimulationStep(simulations1A.get(0).getCompanyId());
        simulationService.adjustCreditLine(companySimulationStep1A.get().getId(), AdjustCreditLineDto.builder().direction(CreditEventDirection.INCREASE).amount(Money.of("CHF", 10000000)).executionMonth(companySimulationStep1A.get().getSimulationMonth()).build());
        simulationService.buildStorage(companySimulationStep1A.get().getId(), BuildStorageDto.builder().capacity(1000).executionMonth(companySimulationStep1A.get().getSimulationMonth()).build());
        simulationService.buildFactory(companySimulationStep1A.get().getId(), BuildFactoryDto.builder().productionLines(5).executionMonth(companySimulationStep1A.get().getSimulationMonth()).build());
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
        simulationService.buyRawMaterial(companySimulationStep2A.get().getId(), BuyRawMaterialDto.builder().amount(10000).executionMonth(companySimulationStep2A.get().getSimulationMonth()).build());
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
        {
            userManagementService.login(MAX, ((StubSendMailServiceImpl) sendMailService).getPassword(MAX));
            List<OpenUserSimulationDto> simulations3A = simulationService.getOpenSimulationsForUser(MAX);
            Optional<CompanySimulationStepDto> companySimulationStep3A = simulationService.getCurrentCompanySimulationStep(simulations3A.get(0).getCompanyId());
            simulationService.enterMarket(companySimulationStep3A.get().getId(),
                    EnterMarketDto.builder().marketId(companySimulationStep3A.get().getMarkets().get(0).getId()).intentedProductSales(1000).offeredPrice(Money.of("CHF", 50)).executionMonth(companySimulationStep3A.get().getSimulationMonth()).build());
            simulationService.finishMoveFor(companySimulationStep3A.get().getId());
            userManagementService.logout();
        }
        //
        // Step 3 for Company B
        //
        {
            userManagementService.login(RETO, ((StubSendMailServiceImpl) sendMailService).getPassword(RETO));
            List<OpenUserSimulationDto> simulations3B = simulationService.getOpenSimulationsForUser(RETO);
            Optional<CompanySimulationStepDto> companySimulationStep3B = simulationService.getCurrentCompanySimulationStep(simulations3B.get(0).getCompanyId());
            simulationService.finishMoveFor(companySimulationStep3B.get().getId());
            userManagementService.logout();
        }
        //
        // Step 3 for Company C
        //
        {
            userManagementService.login(FELIX, ((StubSendMailServiceImpl) sendMailService).getPassword(FELIX));
            List<OpenUserSimulationDto> simulations3C = simulationService.getOpenSimulationsForUser(FELIX);
            Optional<CompanySimulationStepDto> companySimulationStep3C = simulationService.getCurrentCompanySimulationStep(simulations3C.get(0).getCompanyId());
            simulationService.finishMoveFor(companySimulationStep3C.get().getId());
            userManagementService.logout();
        }
        //
        // Step 4 for Company A
        //
        userManagementService.login(MAX, ((StubSendMailServiceImpl) sendMailService).getPassword(MAX));
        List<OpenUserSimulationDto> simulations4A = simulationService.getOpenSimulationsForUser(MAX);
        assertTrue(simulations4A.isEmpty());
        userManagementService.logout();
        //
        // Get simulation statistics
        //
        userManagementService.login(FELIX, ((StubSendMailServiceImpl) sendMailService).getPassword(FELIX));
        List<CompletedUserSimulationDto> completedSimulations = simulationService.getCompletedSimulationsForUser(FELIX);
        SimulationStatisticsDto simulationStatisticsDto = simulationService.getSimulationStatistics(completedSimulations.get(0).getSimulationId());
        assertEquals(0, simulationStatisticsDto.getTotalSoldProducts());
        userManagementService.logout();
        //
        // Login as game user, Start next simulation
        //
        userManagementService.login("user", "e*Wasdf_erwer23");
        SimulationDto simulation = simulationService.getNextAvailableSimulationForOwner("user").get();
        simulation.setName("OK, now to the second simulation...");
        simulation.setStartMonth(YearMonth.of(2023, 1));
        simulation.setNrOfSteps(100);
        simulation.addCompany(CompanyDto.builder().name("Company One").users(Arrays.asList(LoginDto.builder().email(MAX).build(), LoginDto.builder().email("kurt.gruen@bluewin.ch").build())).build());
        simulation.addCompany(CompanyDto.builder().name("Company Two").users(Arrays.asList(LoginDto.builder().email(RETO).build())).build());
        simulation.addCompany(CompanyDto.builder().name("Company Three").users(Arrays.asList(LoginDto.builder().email(FELIX).build(), LoginDto.builder().email("peter.gross@bluewin.ch").build(), LoginDto.builder().email("beat-huerg.minder@bluewin.ch").build())).build());
        simulationService.updateSimulation(simulation);
        sendMailService.send(userManagementService.getEmailsForNewUsers());
        userManagementService.logout();
        //
        // Conduct Steps
        //
        for (int i = 0; i < 10; i++) {
            //
            // Step for Company One
            //
            userManagementService.login(MAX, ((StubSendMailServiceImpl) sendMailService).getPassword(MAX));
            simulationService.finishMoveFor(simulationService.getCurrentCompanySimulationStep(simulationService.getOpenSimulationsForUser(MAX).get(0).getCompanyId()).get().getId());
            userManagementService.logout();
            //
            // Step for Company Two
            //
            userManagementService.login(RETO, ((StubSendMailServiceImpl) sendMailService).getPassword(RETO));
            simulationService.finishMoveFor(simulationService.getCurrentCompanySimulationStep(simulationService.getOpenSimulationsForUser(RETO).get(0).getCompanyId()).get().getId());
            userManagementService.logout();
            //
            // Step for Company Three
            //
            userManagementService.login(FELIX, ((StubSendMailServiceImpl) sendMailService).getPassword(FELIX));
            simulationService.finishMoveFor(simulationService.getCurrentCompanySimulationStep(simulationService.getOpenSimulationsForUser(FELIX).get(0).getCompanyId()).get().getId());
            userManagementService.logout();
        }
        //
        // Display Database
        //
        DatabaseViewer databaseViewer = new DatabaseViewer(entityManager);
        databaseViewer.logDatabase();
        assertTrue(databaseViewer.classHasNumberOfRecords(Login.class, 8));
        assertTrue(databaseViewer.classHasNumberOfRecords(Simulation.class, 2));
        assertTrue(databaseViewer.classHasNumberOfRecords(Company.class, 6));
        assertTrue(databaseViewer.classHasNumberOfRecords(SimulationStep.class, 13));
        assertTrue(databaseViewer.classHasNumberOfRecords(DistributionStep.class, 1));
        assertTrue(databaseViewer.classHasNumberOfRecords(Factory.class, 1));
        assertTrue(databaseViewer.classHasNumberOfRecords(Storage.class, 1));
        assertTrue(databaseViewer.classHasNumberOfRecords(DistributionInMarket.class, 1));
    }
}
