package com.jore.epoc.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jore.epoc.bo.message.Messages;
import com.jore.epoc.bo.user.User;
import com.jore.epoc.bo.user.UserInCompanyRole;
import com.jore.epoc.dto.LoginDto;
import com.jore.epoc.mapper.LoginMapper;
import com.jore.epoc.repositories.LoginRepository;
import com.jore.epoc.repositories.UserInCompanyRoleRepository;
import com.jore.epoc.services.UserManagementService;
import com.jore.mail.Mail;

import lombok.extern.log4j.Log4j2;

/*
 * TODO Avoid duplicate users
 * TODO add roles for users with multiples companies (and simulations)
 */
@Log4j2
@Component
public class UserManagementServiceImpl implements UserManagementService {
    private static final String ADMIN_EPOC_CH = "admin@epoc.ch";
    static User loggedInUser = null;
    @Autowired
    LoginRepository loginRepository;
    @Autowired
    UserInCompanyRoleRepository userInCompanyRoleRepository;

    @Override
    public LoginDto createAdmin(LoginDto adminDto) {
        if (loggedInUser == null) {
            throw new IllegalStateException("No admin currently logged in.");
        }
        if (!loggedInUser.isAdmin()) {
            throw new IllegalStateException("Current logged in user is not an admin.");
        }
        User login = LoginMapper.INSTANCE.loginDtoToLogin(adminDto);
        login.setAdmin(true);
        return LoginMapper.INSTANCE.loginToLoginDto(loginRepository.save(login));
    }

    @Override
    @Transactional
    public void createInitialAdmin(String user, String password) {
        User login = new User();
        login.setLogin("admin");
        login.setPassword("g00dPa&word");
        login.setAdmin(true);
        loginRepository.save(login);
    }

    @Override
    public LoginDto createUser(LoginDto userDto) {
        if (loggedInUser == null) {
            throw new IllegalStateException("No admin currently logged in.");
        }
        if (!loggedInUser.isAdmin()) {
            throw new IllegalStateException("Current logged in user is not an admin.");
        }
        User login = LoginMapper.INSTANCE.loginDtoToLogin(userDto);
        login.setAdmin(true);
        return LoginMapper.INSTANCE.loginToLoginDto(loginRepository.save(login));
    }

    @Override
    @Transactional
    public boolean deleteLogin(String login) {
        if (loggedInUser == null) {
            throw new IllegalStateException("No admin currently logged in.");
        }
        if (!loggedInUser.isAdmin()) {
            throw new IllegalStateException("Current logged in user is not an admin.");
        }
        boolean result = false;
        if (loggedInUser != null) {
            if (!loggedInUser.getLogin().equals(login)) {
                loginRepository.deleteByLogin(login);
                result = true;
            }
        }
        return result;
    }

    @Override
    public Collection<Mail> getEmailsForNewUsers() {
        List<Mail> result = new ArrayList<>();
        Iterable<UserInCompanyRole> findByIsInvitationRequired = userInCompanyRoleRepository.findByIsInvitationRequired(true);
        for (UserInCompanyRole userInCompany : findByIsInvitationRequired) {
            userInCompany.setIsInvitationRequired(false);
            userInCompanyRoleRepository.save(userInCompany);
            Mail mail = new Mail();
            mail.setSender(ADMIN_EPOC_CH);
            mail.addToRecipient(userInCompany.getUser().getEmail());
            mail.addCcRecipient(userInCompany.getCompany().getSimulation().getOwner().getEmail());
            mail.setMessageBody(Messages.getMessage("mailSimulationReadyBody", userInCompany.getUser().getPassword()));
            mail.setSubject(Messages.getMessage("mailSimulationReadySubject", userInCompany.getCompany().getSimulation().getName()));
            result.add(mail);
        }
        return result;
    }

    @Override
    public boolean login(String login, String password) {
        logout();
        Optional<User> result = loginRepository.findByLoginAndPassword(login, password);
        if (result.isPresent()) {
            loggedInUser = result.get();
            log.info("Login user " + login + ".");
        } else {
            log.warn("Could not login user " + login + ".");
        }
        return result.isPresent();
    }

    @Override
    public boolean logout() {
        boolean result = false;
        if (loggedInUser != null) {
            result = true;
            loggedInUser = null;
        }
        return result;
    }
}
