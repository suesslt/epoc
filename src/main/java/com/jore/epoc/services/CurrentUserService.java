package com.jore.epoc.services;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.jore.epoc.dto.UserDto;

import jakarta.validation.Valid;

public interface CurrentUserService extends UserDetailsService {
    UserDto createAdmin(@Valid UserDto admin);

    Optional<UserDto> getAuthenticatedUser();
    //    void login(@NotEmpty String login, @NotEmpty String password);
    //    void logout();
}
