package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.DistributionInMarket;
import com.jore.epoc.bo.Factory;
import com.jore.epoc.bo.Simulation;
import com.jore.epoc.bo.Storage;
import com.jore.epoc.bo.settings.EpocSetting;
import com.jore.epoc.bo.settings.EpocSettings;
import com.jore.epoc.bo.step.CompanySimulationStep;
import com.jore.epoc.bo.step.DistributionStep;
import com.jore.epoc.bo.step.SimulationStep;
import com.jore.epoc.bo.user.User;
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
import com.jore.mail.Mail;
import com.jore.mail.service.SendMailService;
import com.jore.util.DatabaseViewer;

import jakarta.persistence.EntityManager;

@SpringBootTest
class EpocApplicationTests {
    private static final int NR_OF_SIM_STEPS = 120;
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
    private StaticDataService staticDataService;
    private SendMailService sendMailService = new StubSendMailServiceImpl();

    @Test
    public void testFullGameForTenYearsWithOneYearSteps() {
        DatabaseViewer databaseViewer = new DatabaseViewer(entityManager);
        //
        // Create the system user
        //
        userManagementService.createInitialAdmin("admin", "g00dPa&word"); // TODO should throw an exception, doesnt store
        //
        // login as system user
        //
        userManagementService.login("admin", "g00dPa&word");
        userManagementService.createAdmin(LoginDto.builder().login("epocadmin").name("Epoc").email("admin@epoc.ch").password("badpw").build());
        userManagementService.logout();
        //
        // login as applicatoin user
        //
        userManagementService.login("epocadmin", "badpw");
        userManagementService.deleteLogin("admin");
        staticDataService.loadMarkets("markets.xlsx");
        staticDataService.loadSettings("EpocSettings.xlsx");
        userManagementService.createUser(LoginDto.builder().login("simuser").name("Thomas").email("thomas.s@epoc.ch").password("e*Wasdf_erwer23").build());
        userManagementService.logout();
        assertEquals(1, databaseViewer.getNumberOfRecords(EpocSettings.class));
        //
        // login as simulation user, buy simulations and prepare first for usage
        //
        userManagementService.login("simuser", "e*Wasdf_erwer23");
        simulationService.buySimulations(2);
        SimulationDto simulation = simulationService.getNextAvailableSimulationForOwner().get();
        simulation.setName("This is my first real simulation!");
        simulation.setStartMonth(YearMonth.of(2023, 1));
        simulation.setNrOfMonths(NR_OF_SIM_STEPS);
        simulation.addCompany(CompanyDto.builder().name("Company A").users(Arrays.asList(LoginDto.builder().email(MAX).build(), LoginDto.builder().email("kurt.gruen@bluewin.ch").build())).build());
        simulation.addCompany(CompanyDto.builder().name("Company B").users(Arrays.asList(LoginDto.builder().email(RETO).build())).build());
        simulation.addCompany(CompanyDto.builder().name("Company C").users(Arrays.asList(LoginDto.builder().email(FELIX).build(), LoginDto.builder().email("peter.gross@bluewin.ch").build(), LoginDto.builder().email("beat-huerg.minder@bluewin.ch").build())).build());
        simulation.addSetting(SettingDtoBuilder.builder().settingKey(EpocSettings.PASSIVE_STEPS).valueText("11").build());
        simulationService.updateSimulation(simulation);
        Collection<Mail> emailsForNewUsers = userManagementService.getEmailsForNewUsers();
        assertEquals(6, emailsForNewUsers.size());
        sendMailService.send(emailsForNewUsers);
        emailsForNewUsers = userManagementService.getEmailsForNewUsers();
        assertEquals(0, emailsForNewUsers.size());
        userManagementService.logout();
        assertEquals(2, databaseViewer.getNumberOfRecords(EpocSettings.class));
        assertEquals(50, databaseViewer.getNumberOfRecords(EpocSetting.class));
        assertEquals(1, (long) entityManager.createQuery("select count(*) from " + EpocSettings.class.getName() + " where isTemplate = true").getSingleResult());
        assertEquals("0", entityManager.createQuery("select valueText from " + EpocSetting.class.getName() + " where settings.isTemplate = true and settingKey = 'SET0027'").getSingleResult());
        assertEquals("11", entityManager.createQuery("select valueText from " + EpocSetting.class.getName() + " where settings.isTemplate = false and settingKey = 'SET0027'").getSingleResult());
        assertEquals(0, (long) entityManager.createQuery("select count(*) from " + SimulationStep.class.getName()).getSingleResult());
        assertEquals(2, (long) entityManager.createQuery("select count(*) from " + Simulation.class.getName()).getSingleResult());
        //
        // Step 1 for Company A
        //
        userManagementService.login(MAX, getPasswordForUser(MAX));
        List<OpenUserSimulationDto> simulationA1 = simulationService.getOpenSimulationsForUser(MAX);
        Optional<CompanySimulationStepDto> companySimulationStep1A = simulationService.getCurrentCompanySimulationStep(simulationA1.get(0).getCompanyId());
        simulationService.increaseCreditLine(companySimulationStep1A.get().getId(), AdjustCreditLineDto.builder().amount(Money.of("CHF", 100000000)).executionMonth(companySimulationStep1A.get().getSimulationMonth()).build());
        simulationService.buildStorage(companySimulationStep1A.get().getId(), BuildStorageDto.builder().capacity(1000).executionMonth(companySimulationStep1A.get().getSimulationMonth()).build());
        simulationService.buildFactory(companySimulationStep1A.get().getId(), BuildFactoryDto.builder().productionLines(5).executionMonth(companySimulationStep1A.get().getSimulationMonth()).build());
        simulationService.finishMoveFor(companySimulationStep1A.get().getId());
        userManagementService.logout();
        assertEquals(2, (long) entityManager.createQuery("select count(*) from " + Simulation.class.getName()).getSingleResult());
        assertEquals(1, (long) entityManager.createQuery("select count(*) from " + SimulationStep.class.getName()).getSingleResult());
        //
        // Step 1 for Company B
        //
        userManagementService.login(MAX, getPasswordForUser(RETO));
        List<OpenUserSimulationDto> simulationB1 = simulationService.getOpenSimulationsForUser(RETO);
        Optional<CompanySimulationStepDto> companySimulationStepB1 = simulationService.getCurrentCompanySimulationStep(simulationB1.get(0).getCompanyId());
        simulationService.increaseCreditLine(companySimulationStepB1.get().getId(), AdjustCreditLineDto.builder().amount(Money.of("CHF", 100000000)).executionMonth(companySimulationStepB1.get().getSimulationMonth()).build());
        simulationService.buildStorage(companySimulationStepB1.get().getId(), BuildStorageDto.builder().capacity(1000).executionMonth(companySimulationStepB1.get().getSimulationMonth()).build());
        simulationService.buildFactory(companySimulationStepB1.get().getId(), BuildFactoryDto.builder().productionLines(5).executionMonth(companySimulationStepB1.get().getSimulationMonth()).build());
        simulationService.finishMoveFor(companySimulationStepB1.get().getId());
        userManagementService.logout();
        //
        // Step 1 for Company C
        //
        userManagementService.login(MAX, getPasswordForUser(FELIX));
        List<OpenUserSimulationDto> simulationC1 = simulationService.getOpenSimulationsForUser(FELIX);
        Optional<CompanySimulationStepDto> companySimulationStepC1 = simulationService.getCurrentCompanySimulationStep(simulationC1.get(0).getCompanyId());
        simulationService.increaseCreditLine(companySimulationStepC1.get().getId(), AdjustCreditLineDto.builder().amount(Money.of("CHF", 100000000)).executionMonth(companySimulationStepC1.get().getSimulationMonth()).build());
        simulationService.buildStorage(companySimulationStepC1.get().getId(), BuildStorageDto.builder().capacity(1000).executionMonth(companySimulationStepC1.get().getSimulationMonth()).build());
        simulationService.buildFactory(companySimulationStepC1.get().getId(), BuildFactoryDto.builder().productionLines(5).executionMonth(companySimulationStepC1.get().getSimulationMonth()).build());
        simulationService.finishMoveFor(companySimulationStepC1.get().getId());
        userManagementService.logout();
        //
        // Checks after first full step
        //
        assertEquals(13, (long) entityManager.createQuery("select count(*) from " + SimulationStep.class.getName()).getSingleResult());
        assertEquals(39, (long) entityManager.createQuery("select count(*) from " + CompanySimulationStep.class.getName()).getSingleResult());
    }

    //    @Test
    public void testShortSimulationAndOpeningOfNew() {
        userManagementService.createInitialAdmin("admin", "g00dPa&word");
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
        userManagementService.createUser(LoginDto.builder().login("user").name("Thomas").email("thomas.s@epoc.ch").password("e*Wasdf_erwer23").build());
        userManagementService.logout();
        //
        // Login as game user, buy simulations, create companies with users
        //
        {
            userManagementService.login("user", "e*Wasdf_erwer23");
            simulationService.buySimulations(2);
            SimulationDto simulation = simulationService.getNextAvailableSimulationForOwner().get();
            simulation.setName("This is my first real simulation!");
            simulation.setStartMonth(YearMonth.of(2023, 1));
            simulation.setNrOfMonths(NR_OF_SIM_STEPS);
            simulation.addCompany(CompanyDto.builder().name("Company A").users(Arrays.asList(LoginDto.builder().email(MAX).build(), LoginDto.builder().email("kurt.gruen@bluewin.ch").build())).build());
            simulation.addCompany(CompanyDto.builder().name("Company B").users(Arrays.asList(LoginDto.builder().email(RETO).build())).build());
            simulation.addCompany(CompanyDto.builder().name("Company C").users(Arrays.asList(LoginDto.builder().email(FELIX).build(), LoginDto.builder().email("peter.gross@bluewin.ch").build(), LoginDto.builder().email("beat-huerg.minder@bluewin.ch").build())).build());
            simulation.addSetting(SettingDtoBuilder.builder().build());
            simulationService.updateSimulation(simulation);
            sendMailService.send(userManagementService.getEmailsForNewUsers());
            userManagementService.logout();
        }
        //
        // Step 1 for Company A
        //
        userManagementService.login(MAX, getPasswordForUser(MAX));
        List<OpenUserSimulationDto> simulations1A = simulationService.getOpenSimulationsForUser(MAX);
        Optional<CompanySimulationStepDto> companySimulationStep1A = simulationService.getCurrentCompanySimulationStep(simulations1A.get(0).getCompanyId());
        simulationService.increaseCreditLine(companySimulationStep1A.get().getId(), AdjustCreditLineDto.builder().amount(Money.of("CHF", 100000000)).executionMonth(companySimulationStep1A.get().getSimulationMonth()).build());
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
        userManagementService.login(MAX, getPasswordForUser(MAX));
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
        userManagementService.login(MAX, getPasswordForUser(MAX));
        List<OpenUserSimulationDto> simulations3A = simulationService.getOpenSimulationsForUser(MAX);
        Optional<CompanySimulationStepDto> companySimulationStep3A = simulationService.getCurrentCompanySimulationStep(simulations3A.get(0).getCompanyId());
        simulationService.enterMarket(companySimulationStep3A.get().getId(),
                EnterMarketDto.builder().marketId(companySimulationStep3A.get().getMarkets().get(0).getId()).intentedProductSales(1000).offeredPrice(Money.of("CHF", 50)).executionMonth(companySimulationStep3A.get().getSimulationMonth()).build());
        simulationService.setIntentedSalesAndPrice(companySimulationStep3A.get().getId(), companySimulationStep3A.get().getMarkets().get(0).getId(), 1000, Money.of("CHF", 50), companySimulationStep1A.get().getSimulationMonth());
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
        // Repeat getting Step 3 for Company B
        //
        userManagementService.login(RETO, ((StubSendMailServiceImpl) sendMailService).getPassword(RETO));
        List<OpenUserSimulationDto> simulations3BR = simulationService.getOpenSimulationsForUser(RETO);
        Optional<CompanySimulationStepDto> companySimulationStep3BR = simulationService.getCurrentCompanySimulationStep(simulations3BR.get(0).getCompanyId());
        assertEquals(companySimulationStep3B.get().getId(), companySimulationStep3BR.get().getId());
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
        userManagementService.login(MAX, getPasswordForUser(MAX));
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
        SimulationDto simulation = simulationService.getNextAvailableSimulationForOwner().get();
        simulation.setName("OK, now to the second simulation...");
        simulation.setStartMonth(YearMonth.of(2023, 1));
        simulation.setNrOfMonths(100);
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
            userManagementService.login(MAX, getPasswordForUser(MAX));
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
        assertEquals(8, databaseViewer.getNumberOfRecords(User.class));
        assertEquals(2, databaseViewer.getNumberOfRecords(Simulation.class));
        assertEquals(6, databaseViewer.getNumberOfRecords(Company.class));
        assertEquals(14, databaseViewer.getNumberOfRecords(SimulationStep.class)); // TODO why 14? Seems to be wrong
        assertEquals(1, databaseViewer.getNumberOfRecords(DistributionStep.class));
        assertEquals(1, databaseViewer.getNumberOfRecords(Factory.class));
        assertEquals(1, databaseViewer.getNumberOfRecords(Storage.class));
        assertEquals(1, databaseViewer.getNumberOfRecords(DistributionInMarket.class));
    }

    // This is a hack to get the password created by the send mail service
    private String getPasswordForUser(String user) {
        return ((StubSendMailServiceImpl) sendMailService).getPassword(user);
    }
}
