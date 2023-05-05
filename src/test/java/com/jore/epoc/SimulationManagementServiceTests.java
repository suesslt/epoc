package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jore.epoc.bo.user.User;
import com.jore.epoc.services.SimulationService;
import com.jore.epoc.services.StaticDataService;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
class SimulationManagementServiceTests {
    @Autowired
    public SimulationService simulationManagementService;
    @Autowired
    private StaticDataService staticDataService;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @Transactional
    public void insertSystemAdministrator() {
        entityManager.createQuery("delete from Login");
        User login = new User();
        login.setLogin("User");
        login.setPassword("g00dPa&word");
        login.setAdmin(false);
        entityManager.persist(login);
        staticDataService.loadSettings("EpocSettings.xlsx");
    }

    @Test
    @Transactional
    void testBuySimulations() {
        simulationManagementService.buySimulations("User", 10);
        assertEquals(10, simulationManagementService.countAvailableSimulations("User"));
    }
}
