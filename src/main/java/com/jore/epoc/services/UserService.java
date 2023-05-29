package com.jore.epoc.services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.jore.epoc.dto.UserDto;
import com.jore.mail.Mail;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public interface UserService {
    void delete(UserDto user);

    void deleteByUsername(@NotNull @NotEmpty String username);

    List<UserDto> getAllFiltered(String filterText);

    Optional<UserDto> getById(@NotNull Long id);

    Optional<UserDto> getByUsername(@NotNull @NotEmpty String username);

    Collection<Mail> getEmailsForNewUsers();

    UserDto saveUser(@Valid UserDto user);
}