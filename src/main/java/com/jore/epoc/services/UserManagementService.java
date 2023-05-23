package com.jore.epoc.services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;

import com.jore.epoc.dto.UserDto;
import com.jore.mail.Mail;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public interface UserManagementService {
    UserDto createAdmin(@Valid UserDto admin);

    void createInitialAdmin(@NotEmpty String user, @NotEmpty String password); // TODO only required for Test Cases

    void delete(UserDto user);

    boolean deleteLogin(@NotEmpty String login);

    List<UserDto> getAllFiltered(String filterText);

    Optional<UserDto> getAuthenticatedUser();

    Optional<UserDto> getById(Long id);

    UserDto getByUsername(String username);

    Collection<Mail> getEmailsForNewUsers();

    List<UserDto> list(PageRequest of);

    boolean login(@NotEmpty String login, @NotEmpty String password);

    boolean logout();

    UserDto saveUser(@Valid UserDto user);
}