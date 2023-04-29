package com.jore.epoc.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jore.epoc.bo.Login;
import com.jore.epoc.bo.UserInCompanyRole;
import com.jore.epoc.dto.LoginDto;
import com.jore.epoc.mapper.LoginMapper;
import com.jore.epoc.repositories.LoginRepository;
import com.jore.epoc.repositories.UserInCompanyRoleRepository;
import com.jore.epoc.services.UserManagementService;
import com.jore.mail.Mail;

/*
 * TODO Avoid duplicate users
 * TODO add roles for users with multiples companies (and simulations)
 */
@Component
public class UserManagementServiceImpl implements UserManagementService {
    private static final String ADMIN_EPOC_CH = "admin@epoc.ch";
    @Autowired
    LoginRepository loginRepository;
    @Autowired
    UserInCompanyRoleRepository userInCompanyRoleRepository;
    Login userLoggedIn = null;

    @Override
    public LoginDto createAdmin(LoginDto adminDto) {
        if (userLoggedIn == null) {
            throw new IllegalStateException("No admin currently logged in.");
        }
        if (!userLoggedIn.isAdmin()) {
            throw new IllegalStateException("Current logged in user is not an admin.");
        }
        Login login = LoginMapper.INSTANCE.loginDtoToLogin(adminDto);
        login.setAdmin(true);
        return LoginMapper.INSTANCE.loginToLoginDto(loginRepository.save(login));
    }

    @Override
    @Transactional
    public void createInitialUser(String user, String password) {
        Login login = new Login();
        login.setLogin("admin");
        login.setPassword("g00dPa&word");
        login.setAdmin(true);
        loginRepository.save(login);
    }

    @Override
    public LoginDto createUser(LoginDto userDto) {
        Login login = LoginMapper.INSTANCE.loginDtoToLogin(userDto);
        login.setAdmin(true);
        return LoginMapper.INSTANCE.loginToLoginDto(loginRepository.save(login));
    }

    @Override
    @Transactional
    public boolean deleteLogin(String login) {
        if (userLoggedIn == null) {
            throw new IllegalStateException("No admin currently logged in.");
        }
        if (!userLoggedIn.isAdmin()) {
            throw new IllegalStateException("Current logged in user is not an admin.");
        }
        boolean result = false;
        if (userLoggedIn != null) {
            if (!userLoggedIn.getLogin().equals(login)) {
                loginRepository.deleteByLogin(login);
                result = true;
            }
        }
        return result;
    }

    @Override
    public Iterable<Mail> getEmailsForNewUsers() {
        List<Mail> result = new ArrayList<>();
        Iterable<UserInCompanyRole> findByIsInvitationRequired = userInCompanyRoleRepository.findByIsInvitationRequired(true);
        for (UserInCompanyRole userInCompany : findByIsInvitationRequired) {
            userInCompany.setIsInvitationRequired(false);
            userInCompanyRoleRepository.save(userInCompany);
            Mail mail = new Mail();
            mail.setSender(ADMIN_EPOC_CH);
            mail.addToRecipient(userInCompany.getUser().getEmail());
            mail.addCcRecipient(userInCompany.getCompany().getSimulation().getOwner().getEmail());
            mail.setMessageBody("Please login and use password: " + userInCompany.getUser().getPassword());
            mail.setSubject("Your simulation '" + userInCompany.getCompany().getSimulation().getName() + "' is ready");
            result.add(mail);
        }
        return result;
    }

    @Override
    public boolean login(String login, String password) {
        logout();
        Optional<Login> result = loginRepository.findByLoginAndPassword(login, password);
        if (result.isPresent()) {
            userLoggedIn = result.get();
        }
        return result.isPresent();
    }

    @Override
    public boolean logout() {
        boolean result = false;
        if (userLoggedIn != null) {
            result = true;
            userLoggedIn = null;
        }
        return result;
    }
}
