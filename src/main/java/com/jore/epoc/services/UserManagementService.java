package com.jore.epoc.services;

import java.util.Collection;

import com.jore.epoc.dto.UserDto;
import com.jore.mail.Mail;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public interface UserManagementService {
    UserDto createAdmin(@Valid UserDto admin);

    void createInitialAdmin(@NotEmpty String user, @NotEmpty String password); // TODO only required for Test Cases

    boolean deleteLogin(@NotEmpty String login);

    Collection<Mail> getEmailsForNewUsers();

    boolean login(@NotEmpty String login, @NotEmpty String password);

    boolean logout();

    UserDto saveUser(@Valid UserDto user);
}
