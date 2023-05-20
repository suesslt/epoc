package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

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
import com.jore.epoc.dto.IncreaseProductivityDto;
import com.jore.epoc.dto.IncreaseQualityDto;
import com.jore.epoc.dto.IntendedSalesAndPriceDto;
import com.jore.epoc.dto.UserDto;
import com.jore.epoc.dto.OpenUserSimulationDto;
import com.jore.epoc.dto.RunMarketingCampaignDto;
import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.dto.SimulationStatisticsDto;
import com.jore.epoc.services.SimulationService;
import com.jore.epoc.services.StaticDataService;
import com.jore.epoc.services.UserManagementService;
import com.jore.mail.Mail;
import com.jore.mail.service.SendMailService;
import com.jore.util.DatabaseViewer;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("testdb")
class EpocApplicationTests {
    private static final String CHF = "CHF";
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
        ((StubSendMailServiceImpl) sendMailService).clear();
        //
        // Create the system user
        //
        userManagementService.createInitialAdmin("admin", "g00dPa&word"); // TODO should throw an exception, doesnt store
        //
        // login as system user
        //
        assertThrows(ConstraintViolationException.class, () -> userManagementService.login("admin", ""));
        userManagementService.login("admin", "g00dPa&word");
        userManagementService.createAdmin(UserDto.builder().login("epocadmin").name("Epoc").email("admin@epoc.ch").password("badpw").build());
        userManagementService.logout();
        //
        // login as applicatoin user
        //
        userManagementService.login("epocadmin", "badpw");
        userManagementService.deleteLogin("admin");
        assertThrows(ConstraintViolationException.class, () -> staticDataService.loadMarkets(""));
        assertThrows(ConstraintViolationException.class, () -> staticDataService.loadMarkets(null));
        staticDataService.loadMarkets("markets.xlsx");
        staticDataService.loadSettings("EpocSettings.xlsx");
        assertThrows(ConstraintViolationException.class, () -> userManagementService.saveUser(UserDto.builder().login("simuser").name("Thomas").email("thomas.sepoc.ch").password("e*Wasdf_erwer23").build()));
        userManagementService.saveUser(UserDto.builder().login("simuser").name("Thomas").email("thomas.s@epoc.ch").password("e*Wasdf_erwer23").build());
        userManagementService.logout();
        assertEquals(1, databaseViewer.getNumberOfRecords(EpocSettings.class));
        //
        // login as simulation user, buy simulations and prepare first for usage
        //
        userManagementService.login("simuser", "e*Wasdf_erwer23");
        assertThrows(ConstraintViolationException.class, () -> simulationService.buySimulations(0));
        simulationService.buySimulations(2);
        SimulationDto simulation = simulationService.getNextAvailableSimulationForOwner().get();
        simulation.setName("This is my first real simulation!");
        simulation.setStartMonth(YearMonth.of(2023, 1));
        simulation.setNrOfMonths(NR_OF_SIM_STEPS);
        simulation.addCompany(CompanyDto.builder().name("Company A").users(Arrays.asList(UserDto.builder().email(MAX).build(), UserDto.builder().email("kurt.gruen@bluewin.ch").build())).build());
        simulation.addCompany(CompanyDto.builder().name("Company B").users(Arrays.asList(UserDto.builder().email(RETO).build())).build());
        simulation.addCompany(CompanyDto.builder().name("Company C").users(Arrays.asList(UserDto.builder().email(FELIX).build(), UserDto.builder().email("peter.gross@bluewin.ch").build(), UserDto.builder().email("beat-huerg.minder@bluewin.ch").build())).build());
        simulation.addSetting(SettingDtoBuilder.builder().settingKey(EpocSettings.PASSIVE_STEPS).valueText("12").build());
        simulationService.updateSimulation(simulation);
        Collection<Mail> emailsForNewUsers = userManagementService.getEmailsForNewUsers();
        assertEquals(6, emailsForNewUsers.size());
        sendMailService.send(emailsForNewUsers);
        emailsForNewUsers = userManagementService.getEmailsForNewUsers();
        assertEquals(0, emailsForNewUsers.size());
        userManagementService.logout();
        assertEquals(2, databaseViewer.getNumberOfRecords(EpocSettings.class));
        assertEquals(58, databaseViewer.getNumberOfRecords(EpocSetting.class));
        assertEquals(1, (long) entityManager.createQuery("select count(*) from " + EpocSettings.class.getName() + " where isTemplate = true").getSingleResult());
        assertEquals("1", entityManager.createQuery("select valueText from " + EpocSetting.class.getName() + " where settings.isTemplate = true and settingKey = 'SET0027'").getSingleResult());
        assertEquals("12", entityManager.createQuery("select valueText from " + EpocSetting.class.getName() + " where settings.isTemplate = false and settingKey = 'SET0027'").getSingleResult());
        assertEquals(0, (long) entityManager.createQuery("select count(*) from " + SimulationStep.class.getName()).getSingleResult());
        assertEquals(2, (long) entityManager.createQuery("select count(*) from " + Simulation.class.getName()).getSingleResult());
        //
        // Step 1 for Company A
        //
        userManagementService.login(MAX, getPasswordForUser(MAX));
        List<OpenUserSimulationDto> simulationA1 = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStepA1 = simulationService.getCurrentCompanySimulationStep(simulationA1.get(0).getCompanyId());
        simulationService.increaseCreditLine(AdjustCreditLineDto.builder().companyId(simulationA1.get(0).getCompanyId()).amount(Money.of(CHF, 100000000)).executionMonth(companySimulationStepA1.get().getSimulationMonth()).build());
        simulationService.buildStorage(BuildStorageDto.builder().companyId(simulationA1.get(0).getCompanyId()).capacity(1000).executionMonth(companySimulationStepA1.get().getSimulationMonth()).build());
        simulationService.buildFactory(BuildFactoryDto.builder().companyId(simulationA1.get(0).getCompanyId()).productionLines(5).executionMonth(companySimulationStepA1.get().getSimulationMonth()).build());
        assertThrows(ConstraintViolationException.class, () -> simulationService.buildFactory(BuildFactoryDto.builder().companyId(simulationA1.get(0).getCompanyId()).productionLines(15).executionMonth(companySimulationStepA1.get().getSimulationMonth()).build()));
        simulationService.enterMarket(
                EnterMarketDto.builder().companyId(simulationA1.get(0).getCompanyId()).marketId(companySimulationStepA1.get().getMarkets().get(0).getId()).intentedProductSales(1000).offeredPrice(Money.of(CHF, 50)).executionMonth(companySimulationStepA1.get().getSimulationMonth()).build());
        simulationService.finishMoveFor(companySimulationStepA1.get().getId());
        userManagementService.logout();
        assertEquals(2, (long) entityManager.createQuery("select count(*) from " + Simulation.class.getName()).getSingleResult());
        assertEquals(1, (long) entityManager.createQuery("select count(*) from " + SimulationStep.class.getName()).getSingleResult());
        //
        // Step 1 for Company B
        //
        userManagementService.login(RETO, getPasswordForUser(RETO));
        List<OpenUserSimulationDto> simulationB1 = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStepB1 = simulationService.getCurrentCompanySimulationStep(simulationB1.get(0).getCompanyId());
        simulationService.increaseCreditLine(AdjustCreditLineDto.builder().companyId(simulationB1.get(0).getCompanyId()).amount(Money.of(CHF, 100000000)).executionMonth(companySimulationStepB1.get().getSimulationMonth()).build());
        simulationService.buildStorage(BuildStorageDto.builder().companyId(simulationB1.get(0).getCompanyId()).capacity(1000).executionMonth(companySimulationStepB1.get().getSimulationMonth()).build());
        simulationService.buildFactory(BuildFactoryDto.builder().companyId(simulationB1.get(0).getCompanyId()).productionLines(5).executionMonth(companySimulationStepB1.get().getSimulationMonth()).build());
        simulationService.enterMarket(
                EnterMarketDto.builder().companyId(simulationB1.get(0).getCompanyId()).marketId(companySimulationStepB1.get().getMarkets().get(0).getId()).intentedProductSales(1000).offeredPrice(Money.of(CHF, 50)).executionMonth(companySimulationStepB1.get().getSimulationMonth()).build());
        simulationService.finishMoveFor(companySimulationStepB1.get().getId());
        userManagementService.logout();
        //
        // Step 1 for Company C
        //
        userManagementService.login(FELIX, getPasswordForUser(FELIX));
        List<OpenUserSimulationDto> simulationC1 = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStepC1 = simulationService.getCurrentCompanySimulationStep(simulationC1.get(0).getCompanyId());
        simulationService.increaseCreditLine(AdjustCreditLineDto.builder().companyId(simulationC1.get(0).getCompanyId()).amount(Money.of(CHF, 100000000)).executionMonth(companySimulationStepC1.get().getSimulationMonth()).build());
        assertThrows(ConstraintViolationException.class, () -> simulationService.buildStorage(BuildStorageDto.builder().companyId(simulationC1.get(0).getCompanyId()).capacity(1000).executionMonth(YearMonth.of(2022, 12)).build()));
        simulationService.buildStorage(BuildStorageDto.builder().companyId(simulationC1.get(0).getCompanyId()).capacity(1000).executionMonth(companySimulationStepC1.get().getSimulationMonth()).build());
        simulationService.buildFactory(BuildFactoryDto.builder().companyId(simulationC1.get(0).getCompanyId()).productionLines(5).executionMonth(companySimulationStepC1.get().getSimulationMonth()).build());
        simulationService.finishMoveFor(companySimulationStepC1.get().getId());
        // After step has finshed - TODO Validate and do not allow
        simulationService.enterMarket(
                EnterMarketDto.builder().companyId(simulationC1.get(0).getCompanyId()).marketId(companySimulationStepC1.get().getMarkets().get(0).getId()).intentedProductSales(1000).offeredPrice(Money.of(CHF, 50)).executionMonth(companySimulationStepC1.get().getSimulationMonth()).build());
        userManagementService.logout();
        //
        // Checks after first full step
        //
        assertTrue(simulationService.getCompletedSimulationsForUser(MAX).isEmpty());
        assertEquals(13, (long) entityManager.createQuery("select count(*) from " + SimulationStep.class.getName()).getSingleResult());
        assertEquals(39, (long) entityManager.createQuery("select count(*) from " + CompanySimulationStep.class.getName()).getSingleResult());
        //
        // Step 2 for Company A
        //
        userManagementService.login(MAX, getPasswordForUser(MAX));
        List<OpenUserSimulationDto> simulationA2 = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStepA2 = simulationService.getCurrentCompanySimulationStep(simulationA2.get(0).getCompanyId());
        assertEquals(4, companySimulationStepA2.get().getMessages().size());
        simulationService.buyRawMaterial(BuyRawMaterialDto.builder().companyId(simulationA2.get(0).getCompanyId()).amount(1000).executionMonth(YearMonth.of(2024, 3)).build());
        simulationService.increaseProductivity(IncreaseProductivityDto.builder().companyId(simulationA2.get(0).getCompanyId()).increaseProductivityAmount(Money.of(CHF, 10000)).executionMonth(companySimulationStepC1.get().getSimulationMonth()).build());
        simulationService.increaseQuality(IncreaseQualityDto.builder().companyId(simulationA2.get(0).getCompanyId()).increaseQualityAmount(Money.of(CHF, 10000)).executionMonth(companySimulationStepC1.get().getSimulationMonth()).build());
        simulationService.runMarketingCampaign(RunMarketingCampaignDto.builder().companyId(simulationA2.get(0).getCompanyId()).campaignAmount(Money.of(CHF, 10000)).executionMonth(companySimulationStepC1.get().getSimulationMonth()).build());
        simulationService.finishMoveFor(companySimulationStepA2.get().getId());
        userManagementService.logout();
        //
        // Step 2 for Company B
        //
        userManagementService.login(RETO, getPasswordForUser(RETO));
        List<OpenUserSimulationDto> simulationB2 = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStepB2 = simulationService.getCurrentCompanySimulationStep(simulationB2.get(0).getCompanyId());
        assertEquals(4, companySimulationStepB2.get().getMessages().size());
        simulationService.buyRawMaterial(BuyRawMaterialDto.builder().companyId(simulationB2.get(0).getCompanyId()).amount(1000).executionMonth(YearMonth.of(2024, 3)).build());
        simulationService.decreaseCreditLine(AdjustCreditLineDto.builder().companyId(simulationB2.get(0).getCompanyId()).amount(Money.of(CHF, 10)).executionMonth(companySimulationStepB2.get().getSimulationMonth()).build());
        simulationService.increaseProductivity(IncreaseProductivityDto.builder().companyId(simulationB2.get(0).getCompanyId()).increaseProductivityAmount(Money.of("CHF", 100000000)).executionMonth(companySimulationStepB2.get().getSimulationMonth()).build());
        simulationService.increaseQuality(IncreaseQualityDto.builder().companyId(simulationB2.get(0).getCompanyId()).increaseQualityAmount(Money.of("CHF", 100000000)).executionMonth(companySimulationStepB2.get().getSimulationMonth()).build());
        simulationService.runMarketingCampaign(RunMarketingCampaignDto.builder().companyId(simulationB2.get(0).getCompanyId()).campaignAmount(Money.of("CHF", 100000000)).executionMonth(companySimulationStepB2.get().getSimulationMonth()).build());
        simulationService.finishMoveFor(companySimulationStepB2.get().getId());
        userManagementService.logout();
        //
        // Step 2 for Company C
        //
        userManagementService.login(FELIX, getPasswordForUser(FELIX));
        List<OpenUserSimulationDto> simulationC2 = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStepC2 = simulationService.getCurrentCompanySimulationStep(simulationC2.get(0).getCompanyId());
        simulationService.finishMoveFor(companySimulationStepC2.get().getId());
        userManagementService.logout();
    }

    @Test
    public void testShortSimulationAndOpeningOfNew() {
        ((StubSendMailServiceImpl) sendMailService).clear();
        userManagementService.createInitialAdmin("admin", "g00dPa&word");
        //
        // Create user for simulation and delete ad min
        //
        userManagementService.login("admin", "g00dPa&word");
        userManagementService.createAdmin(UserDto.builder().login("epocadmin").name("Epoc").email("admin@epoc.ch").password("badpw").build());
        userManagementService.logout();
        userManagementService.login("epocadmin", "badpw");
        userManagementService.deleteLogin("admin");
        staticDataService.loadMarkets("markets.xlsx");
        staticDataService.loadSettings("EpocSettings.xlsx");
        userManagementService.saveUser(UserDto.builder().login("user").name("Thomas").email("thomas.s@epoc.ch").password("e*Wasdf_erwer23").build());
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
            simulation.setNrOfMonths(3);
            simulation.addCompany(CompanyDto.builder().name("Company A").users(Arrays.asList(UserDto.builder().email(MAX).build(), UserDto.builder().email("kurt.gruen@bluewin.ch").build())).build());
            simulation.addCompany(CompanyDto.builder().name("Company B").users(Arrays.asList(UserDto.builder().email(RETO).build())).build());
            simulation.addCompany(CompanyDto.builder().name("Company C").users(Arrays.asList(UserDto.builder().email(FELIX).build(), UserDto.builder().email("peter.gross@bluewin.ch").build(), UserDto.builder().email("beat-huerg.minder@bluewin.ch").build())).build());
            simulationService.updateSimulation(simulation);
            sendMailService.send(userManagementService.getEmailsForNewUsers());
            userManagementService.logout();
        }
        //
        // Step 1 for Company A
        //
        userManagementService.login(MAX, getPasswordForUser(MAX));
        List<OpenUserSimulationDto> simulations1A = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStep1A = simulationService.getCurrentCompanySimulationStep(simulations1A.get(0).getCompanyId());
        simulationService.increaseCreditLine(AdjustCreditLineDto.builder().companyId(simulations1A.get(0).getCompanyId()).amount(Money.of(CHF, 100000000)).executionMonth(companySimulationStep1A.get().getSimulationMonth()).build());
        simulationService.buildStorage(BuildStorageDto.builder().companyId(simulations1A.get(0).getCompanyId()).capacity(1000).executionMonth(companySimulationStep1A.get().getSimulationMonth()).build());
        simulationService.buildFactory(BuildFactoryDto.builder().companyId(simulations1A.get(0).getCompanyId()).productionLines(5).executionMonth(companySimulationStep1A.get().getSimulationMonth()).build());
        simulationService.finishMoveFor(companySimulationStep1A.get().getId());
        userManagementService.logout();
        //
        // Step 1 for Company B
        //
        userManagementService.login(RETO, getPasswordForUser(RETO));
        List<OpenUserSimulationDto> simulations1B = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStep1B = simulationService.getCurrentCompanySimulationStep(simulations1B.get(0).getCompanyId());
        simulationService.finishMoveFor(companySimulationStep1B.get().getId());
        userManagementService.logout();
        //
        // Step 1 for Company C
        //
        userManagementService.login(FELIX, getPasswordForUser(FELIX));
        List<OpenUserSimulationDto> simulations1C = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStep1C = simulationService.getCurrentCompanySimulationStep(simulations1C.get(0).getCompanyId());
        simulationService.finishMoveFor(companySimulationStep1C.get().getId());
        userManagementService.logout();
        //
        // Step 2 for Company A
        //
        userManagementService.login(MAX, getPasswordForUser(MAX));
        List<OpenUserSimulationDto> simulations2A = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStep2A = simulationService.getCurrentCompanySimulationStep(simulations2A.get(0).getCompanyId());
        simulationService.buyRawMaterial(BuyRawMaterialDto.builder().companyId(simulations2A.get(0).getCompanyId()).amount(1000).executionMonth(companySimulationStep2A.get().getSimulationMonth()).build());
        simulationService.enterMarket(
                EnterMarketDto.builder().companyId(simulations2A.get(0).getCompanyId()).marketId(companySimulationStep2A.get().getMarkets().get(0).getId()).intentedProductSales(1000).offeredPrice(Money.of(CHF, 50)).executionMonth(companySimulationStep2A.get().getSimulationMonth()).build());
        simulationService.finishMoveFor(companySimulationStep2A.get().getId());
        userManagementService.logout();
        //
        // Step 2 for Company B
        //
        userManagementService.login(RETO, getPasswordForUser(RETO));
        List<OpenUserSimulationDto> simulations2B = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStep2B = simulationService.getCurrentCompanySimulationStep(simulations2B.get(0).getCompanyId());
        simulationService.finishMoveFor(companySimulationStep2B.get().getId());
        userManagementService.logout();
        //
        // Step 2 for Company C
        //
        userManagementService.login(FELIX, getPasswordForUser(FELIX));
        List<OpenUserSimulationDto> simulations2C = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStep2C = simulationService.getCurrentCompanySimulationStep(simulations2C.get(0).getCompanyId());
        simulationService.finishMoveFor(companySimulationStep2C.get().getId());
        userManagementService.logout();
        //
        // Step 3 for Company A
        //
        userManagementService.login(MAX, getPasswordForUser(MAX));
        List<OpenUserSimulationDto> simulations3A = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStep3A = simulationService.getCurrentCompanySimulationStep(simulations3A.get(0).getCompanyId());
        //        simulationService.enterMarket(companySimulationStep3A.get().getId(),
        //                EnterMarketDto.builder().marketId(companySimulationStep3A.get().getMarkets().get(0).getId()).intentedProductSales(1000).offeredPrice(Money.of("CHF", 50)).executionMonth(companySimulationStep3A.get().getSimulationMonth()).build());
        simulationService.setIntentedSalesAndPrice(
                IntendedSalesAndPriceDto.builder().companyId(simulations3A.get(0).getCompanyId()).marketId(companySimulationStep3A.get().getMarkets().get(0).getId()).intentedSales(1000).price(Money.of(CHF, 50)).executionMonth(companySimulationStep1A.get().getSimulationMonth()).build());
        simulationService.finishMoveFor(companySimulationStep3A.get().getId());
        userManagementService.logout();
        //
        // Step 3 for Company B
        //
        userManagementService.login(RETO, getPasswordForUser(RETO));
        List<OpenUserSimulationDto> simulations3B = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStep3B = simulationService.getCurrentCompanySimulationStep(simulations3B.get(0).getCompanyId());
        simulationService.finishMoveFor(companySimulationStep3B.get().getId());
        userManagementService.logout();
        //
        // Repeat getting Step 3 for Company B
        //
        userManagementService.login(RETO, getPasswordForUser(RETO));
        List<OpenUserSimulationDto> simulations3BR = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStep3BR = simulationService.getCurrentCompanySimulationStep(simulations3BR.get(0).getCompanyId());
        assertEquals(companySimulationStep3B.get().getId(), companySimulationStep3BR.get().getId());
        userManagementService.logout();
        //
        // Step 3 for Company C
        //
        userManagementService.login(FELIX, getPasswordForUser(FELIX));
        List<OpenUserSimulationDto> simulations3C = simulationService.getOpenSimulationsForUser();
        Optional<CompanySimulationStepDto> companySimulationStep3C = simulationService.getCurrentCompanySimulationStep(simulations3C.get(0).getCompanyId());
        // TODO error happens here as orders are processed here, also for Company A
        simulationService.finishMoveFor(companySimulationStep3C.get().getId());
        userManagementService.logout();
        //
        // Step 4 for Company A
        //
        userManagementService.login(MAX, getPasswordForUser(MAX));
        List<OpenUserSimulationDto> simulations4A = simulationService.getOpenSimulationsForUser();
        assertTrue(simulations4A.isEmpty());
        userManagementService.logout();
        //
        // Get simulation statistics
        //
        userManagementService.login(FELIX, getPasswordForUser(FELIX));
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
        simulation.addCompany(CompanyDto.builder().name("Company One").users(Arrays.asList(UserDto.builder().email(MAX).build(), UserDto.builder().email("kurt.gruen@bluewin.ch").build())).build());
        simulation.addCompany(CompanyDto.builder().name("Company Two").users(Arrays.asList(UserDto.builder().email(RETO).build())).build());
        simulation.addCompany(CompanyDto.builder().name("Company Three").users(Arrays.asList(UserDto.builder().email(FELIX).build(), UserDto.builder().email("peter.gross@bluewin.ch").build(), UserDto.builder().email("beat-huerg.minder@bluewin.ch").build())).build());
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
            simulationService.finishMoveFor(simulationService.getCurrentCompanySimulationStep(simulationService.getOpenSimulationsForUser().get(0).getCompanyId()).get().getId());
            userManagementService.logout();
            //
            // Step for Company Two
            //
            userManagementService.login(RETO, ((StubSendMailServiceImpl) sendMailService).getPassword(RETO));
            simulationService.finishMoveFor(simulationService.getCurrentCompanySimulationStep(simulationService.getOpenSimulationsForUser().get(0).getCompanyId()).get().getId());
            userManagementService.logout();
            //
            // Step for Company Three
            //
            userManagementService.login(FELIX, ((StubSendMailServiceImpl) sendMailService).getPassword(FELIX));
            simulationService.finishMoveFor(simulationService.getCurrentCompanySimulationStep(simulationService.getOpenSimulationsForUser().get(0).getCompanyId()).get().getId());
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
        assertEquals(2, databaseViewer.getNumberOfRecords(DistributionStep.class));
        assertEquals(1, databaseViewer.getNumberOfRecords(Factory.class));
        assertEquals(1, databaseViewer.getNumberOfRecords(Storage.class));
        assertEquals(1, databaseViewer.getNumberOfRecords(DistributionInMarket.class));
    }

    // This is a hack to get the password created by the send mail service
    private String getPasswordForUser(String user) {
        return ((StubSendMailServiceImpl) sendMailService).getPassword(user);
    }
}
