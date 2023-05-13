package com.jore.epoc.services;

import java.util.Collection;

import com.jore.epoc.dto.LoginDto;
import com.jore.mail.Mail;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public interface UserManagementService {
    LoginDto createAdmin(@Valid LoginDto admin);

    void createInitialAdmin(@NotEmpty String user, @NotEmpty String password); // TODO only required for Test Cases

    LoginDto createUser(@Valid LoginDto user);

    boolean deleteLogin(@NotEmpty String login);

    Collection<Mail> getEmailsForNewUsers();

    boolean login(@NotEmpty String login, @NotEmpty String password);

    boolean logout();
}
