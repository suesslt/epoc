package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.jore.epoc.bo.user.User;
import com.jore.epoc.dto.LoginDto;
import com.jore.epoc.services.UserManagementService;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("testdb")
class UserManagementServiceTests {
    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    public void contextLoads() {
        assertNotNull(userManagementService);
        assertNotNull(entityManager);
    }

    @BeforeEach
    @Transactional
    public void insertSystemAdministrator() {
        User login = new User();
        login.setLogin("admin");
        login.setPassword("g00dPa&word");
        login.setAdmin(true);
        entityManager.persist(login);
    }

    @Test
    @Transactional
    public void testCannotDeleteCurrentAdmin() {
        assertTrue(userManagementService.login("admin", "g00dPa&word"));
        assertFalse(userManagementService.deleteLogin("admin"));
    }

    @Test
    @Transactional
    public void testCreateNewAdmin() {
        userManagementService.login("admin", "g00dPa&word");
        LoginDto loginDto = userManagementService.createAdmin(LoginDto.builder().login("epocadmin").name("Epoc").email("admin@epoc.ch").password("badpw").build());
        User storedLogin = entityManager.find(User.class, loginDto.getId());
        assertEquals("epocadmin", storedLogin.getLogin());
    }

    @Test
    @Transactional
    public void testCreateUser() {
        userManagementService.login("admin", "g00dPa&word");
        LoginDto loginDto = userManagementService.createUser(LoginDto.builder().login("user").name("Thomas").email("thomas.s@epoc.ch").password("e*Wasdf_erwer23").build());
        User storedLogin = entityManager.find(User.class, loginDto.getId());
        assertEquals("user", storedLogin.getLogin());
    }

    @Test
    @Transactional
    public void testDeleteAdmin() {
        userManagementService.login("admin", "g00dPa&word");
        userManagementService.createAdmin(LoginDto.builder().login("epocadmin_new").name("Epoc").email("admin@epoc.ch").password("badpw").build());
        userManagementService.logout();
        userManagementService.login("epocadmin_new", "badpw");
        long nr = (long) entityManager.createQuery("select count(*) from Login").getSingleResult();
        assertTrue(userManagementService.deleteLogin("admin"));
        assertEquals(nr - 1, entityManager.createQuery("select count(*) from Login").getSingleResult());
    }

    @Test
    @Transactional
    public void testLoginAndLogout() {
        assertTrue(userManagementService.login("admin", "g00dPa&word"));
        assertTrue(userManagementService.logout());
    }

    @Test
    @Transactional
    public void testLoginAsSystemAdminNoPassword() {
        assertThrows(ConstraintViolationException.class, () -> userManagementService.login("administrator", null));
    }

    @Test
    @Transactional
    public void testLoginAsSystemAdminSuccessful() {
        assertTrue(userManagementService.login("admin", "g00dPa&word"));
    }

    @Test
    @Transactional
    public void testLoginAsSystemAdminWrongLogin() {
        assertFalse(userManagementService.login("administrator", "g00dPa&word"));
    }

    @Test
    @Transactional
    public void testLoginAsSystemAdminWrongPassword() {
        assertFalse(userManagementService.login("administrator", "g00dpa&word"));
    }

    @Test
    @Transactional
    public void testLogoutAfterWrongLogin() {
        assertFalse(userManagementService.login("admin", "g00dpa&word"));
        assertFalse(userManagementService.logout());
    }
}
