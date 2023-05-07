package com.jore.epoc.services;

import java.util.Collection;

import com.jore.epoc.dto.LoginDto;
import com.jore.mail.Mail;

public interface UserManagementService {
    LoginDto createAdmin(LoginDto admin);

    void createInitialAdmin(String user, String password);

    LoginDto createUser(LoginDto user);

    boolean deleteLogin(String login);

    Collection<Mail> getEmailsForNewUsers();

    boolean login(String login, String password);

    boolean logout();
}
