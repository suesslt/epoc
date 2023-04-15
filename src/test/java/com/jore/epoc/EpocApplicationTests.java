package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jore.epoc.dto.CompanyDto;
import com.jore.epoc.dto.CompanySimulationStepDto;
import com.jore.epoc.dto.FactoryOrderDto;
import com.jore.epoc.dto.LoginDto;
import com.jore.epoc.dto.OpenUserSimulationDto;
import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.dto.StorageDto;
import com.jore.epoc.services.SimulationService;
import com.jore.epoc.services.UserManagementService;
import com.jore.mail.service.SendMailService;

import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootTest
class EpocApplicationTests {
    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private SimulationService simulationService;
    @Autowired
    private EntityManager entityManager;
    private SendMailService sendMailService = new StubSendMailServiceImpl();;

    @Test
    public void contextLoads() {
        assertNotNull(userManagementService);
        assertNotNull(entityManager);
    }

    @Test
    public void testUseCasesInOneRow() {
        userManagementService.createInitialUser("admin", "g00dPa&word");
        //
        // Create user for simulation and delete admin
        //
        userManagementService.login("admin", "g00dPa&word");
        userManagementService.createAdmin(LoginDto.builder().login("epocadmin").name("Epoc").email("admin@epoc.ch").password("badpw").build());
        userManagementService.logout();
        userManagementService.login("epocadmin", "badpw");
        userManagementService.deleteLogin("admin");
        userManagementService.logout();
        userManagementService.createUser(LoginDto.builder().login("user").name("Thomas").email("thomas.s@epoc.ch").password("e*Wasdf_erwer23").build());
        userManagementService.logout();
        //
        // Login as game user, buy simulations, create companies with users
        //
        userManagementService.login("user", "e*Wasdf_erwer23");
        simulationService.buySimulations("user", 10);
        SimulationDto simulation = simulationService.getNextAvailableSimulationForUser("user");
        simulation.setName("This is my first real simulation!");
        simulation.setStartMonth(YearMonth.of(2023, 1));
        simulation.addCompany(CompanyDto.builder().name("Company A").users(Arrays.asList(LoginDto.builder().email("max.mara@bluewin.ch").build(), LoginDto.builder().email("kurt.gruen@bluewin.ch").build())).build());
        simulation.addCompany(CompanyDto.builder().name("Company B").users(Arrays.asList(LoginDto.builder().email("reto.straumann@bluewin.ch").build())).build());
        simulation.addCompany(CompanyDto.builder().name("Company C").users(Arrays.asList(LoginDto.builder().email("felix.haeppy@bluewin.ch").build(), LoginDto.builder().email("peter.gross@bluewin.ch").build(), LoginDto.builder().email("beat-huerg.minder@bluewin.ch").build())).build());
        simulationService.updateSimulation(simulation);
        sendMailService.send(userManagementService.getEmailsForNewUsers());
        userManagementService.logout();
        //
        // Step 1 for Company A
        //
        userManagementService.login("max.mara@bluewin.ch", ((StubSendMailServiceImpl) sendMailService).getPassword("max.mara@bluewin.ch"));
        List<OpenUserSimulationDto> simulationsA = simulationService.getOpenSimulationsForUser("max.mara@bluewin.ch");
        CompanySimulationStepDto companySimulationStepA = simulationService.getCurrentCompanySimulationStep(simulationsA.get(0).getCompanyId());
        simulationService.buildFactory(companySimulationStepA.getId(), FactoryOrderDto.builder().productionLines(5).build());
        simulationService.buildStorage(companySimulationStepA.getId(), StorageDto.builder().capacity(1000).build());
        simulationService.finishMoveFor(companySimulationStepA.getId());
        userManagementService.logout();
        //
        // Step 1 for Company B
        //
        userManagementService.login("reto.straumann@bluewin.ch", ((StubSendMailServiceImpl) sendMailService).getPassword("reto.straumann@bluewin.ch"));
        List<OpenUserSimulationDto> simulationsB = simulationService.getOpenSimulationsForUser("reto.straumann@bluewin.ch");
        CompanySimulationStepDto companySimulationStepB = simulationService.getCurrentCompanySimulationStep(simulationsB.get(0).getCompanyId());
        simulationService.finishMoveFor(companySimulationStepB.getId());
        userManagementService.logout();
        DatabaseViewer.logDatabase(entityManager);
//        userManagementService.login("reto.straumann@bluewin.ch", ((StubSendMailServiceImpl) sendMailService).getPassword("reto.straumann@bluewin.ch"));
//        simulationManagementService.finishMoveFor(simulationStepB);
//        userManagementService.logout();
//        userManagementService.login("felix.haeppy@bluewin.ch", ((StubSendMailServiceImpl) sendMailService).getPassword("felix.haeppy@bluewin.ch"));
//        simulationManagementService.finishMoveFor(simulationStepC);
//        userManagementService.logout();
    }
}
