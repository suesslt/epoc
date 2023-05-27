package com.jore.epoc.services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.jore.epoc.dto.UserDto;
import com.jore.mail.Mail;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public interface UserService {
    void delete(UserDto user);

    void deleteByUsername(@NotEmpty String username);

    List<UserDto> getAllFiltered(String filterText);

    Optional<UserDto> getById(Long id);

    Optional<UserDto> getByUsername(String username);

    Collection<Mail> getEmailsForNewUsers();

    UserDto saveUser(@Valid UserDto user);
}