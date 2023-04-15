package com.jore.epoc.services;

import com.jore.epoc.dto.LoginDto;
import com.jore.mail.Mail;

public interface UserManagementService {
    LoginDto createAdmin(LoginDto admin);

    void createInitialUser(String user, String password);

    LoginDto createUser(LoginDto user);

    boolean deleteLogin(String login);

    Iterable<Mail> getEmailsForNewUsers();

    boolean login(String login, String password);

    boolean logout();
}
