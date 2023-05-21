package com.jore.epoc.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.jore.epoc.bo.message.Messages;
import com.jore.epoc.bo.user.User;
import com.jore.epoc.bo.user.UserInCompanyRole;
import com.jore.epoc.dto.UserDto;
import com.jore.epoc.mapper.LoginMapper;
import com.jore.epoc.repositories.LoginRepository;
import com.jore.epoc.repositories.UserInCompanyRoleRepository;
import com.jore.epoc.services.UserManagementService;
import com.jore.mail.Mail;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

/*
 * TODO Avoid duplicate users
 * TODO add roles for users with multiples companies (and simulations)
 */
@Log4j2
@Component
@Validated
@Service
public class UserManagementServiceImpl implements UserManagementService {
    private static final String ADMIN_EPOC_CH = "admin@epoc.ch";
    // TODO must be removed, goal is a stateless service
    static User loggedInUser = null;
    @Autowired
    LoginRepository userRepository;
    @Autowired
    UserInCompanyRoleRepository userInCompanyRoleRepository;
    @Autowired
    EntityManager entityManager;

    @Override
    @Transactional
    public UserDto createAdmin(UserDto adminDto) {
        if (loggedInUser == null) {
            throw new IllegalStateException("No admin currently logged in.");
        }
        if (!loggedInUser.isAdmin()) {
            throw new IllegalStateException("Current logged in user is not an admin.");
        }
        User login = LoginMapper.INSTANCE.loginDtoToLogin(adminDto);
        login.setAdmin(true);
        if (userRepository.findByLogin(adminDto.getLogin()).isPresent()) {
            throw new IllegalStateException();
        }
        return LoginMapper.INSTANCE.loginToLoginDto(userRepository.save(login));
    }

    @Override
    @Transactional
    public void createInitialAdmin(String user, String password) {
        entityManager.createQuery("delete from com.jore.epoc.bo.user.User").executeUpdate();
        User login = new User();
        login.setLogin("admin");
        login.setPassword("g00dPa&word");
        login.setAdmin(true);
        if (userRepository.findByLogin("admin").isPresent()) {
            throw new IllegalStateException();
        }
        userRepository.save(login);
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
                User user = userRepository.findByLogin(login).get();
                userRepository.delete(user);
                //                loginRepository.deleteByLogin(login);
                result = true;
            }
        }
        return result;
    }

    public List<UserDto> getAllUsers() {
        return LoginMapper.INSTANCE.loginToLoginDto(userRepository.findAll());
    }

    public UserDto getById(Long id) {
        return LoginMapper.INSTANCE.loginToLoginDto(userRepository.findById(id.intValue()).get());
    }

    @Override
    @Transactional
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
    @Transactional
    public boolean login(String login, String password) {
        logout();
        Optional<User> result = userRepository.findByLoginAndPassword(login, password);
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

    @Override
    @Transactional
    public UserDto saveUser(@Valid UserDto userDto) {
        String errorData = "{error}";
        UserDto result = null;
        if (userDto.getId() == null) {
            Optional<User> existingLogin = userRepository.findByLogin(userDto.getLogin());
            if (existingLogin.isEmpty()) {
                User login = LoginMapper.INSTANCE.loginDtoToLogin(userDto);
                result = LoginMapper.INSTANCE.loginToLoginDto(userRepository.save(login));
            } else {
                throw new IllegalStateException(errorData);
            }
        } else {
            Optional<User> otherLogin = userRepository.findById(userDto.getId());
            if (otherLogin.isPresent()) {
                if (!otherLogin.get().getLogin().equals(userDto.getLogin())) {
                    Optional<User> existingLogin = userRepository.findByLogin(userDto.getLogin());
                    if (existingLogin.isEmpty()) {
                        User login = LoginMapper.INSTANCE.loginDtoToLogin(userDto);
                        result = LoginMapper.INSTANCE.loginToLoginDto(userRepository.save(login));
                    } else {
                        throw new IllegalStateException(errorData);
                    }
                } else {
                    User login = LoginMapper.INSTANCE.loginDtoToLogin(userDto);
                    result = LoginMapper.INSTANCE.loginToLoginDto(userRepository.save(login));
                }
            } else {
                throw new IllegalStateException(errorData);
            }
        }
        return result;
    }
}
