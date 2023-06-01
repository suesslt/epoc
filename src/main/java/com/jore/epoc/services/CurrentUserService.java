package com.jore.epoc.services;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.jore.epoc.dto.UserDto;

public interface CurrentUserService extends UserDetailsService {
    Optional<UserDto> getAuthenticatedUser();

    Optional<UserDto> getByToken(String token);
}
