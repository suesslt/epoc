package com.jore.epoc.services;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.jore.epoc.dto.UserDto;
import com.jore.epoc.dto.UserTokenDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public interface UserAdminService extends UserDetailsService {
    void deleteUserToken(@NotNull Long userTokenId);

    Optional<UserDto> getAuthenticatedUser();

    Optional<UserTokenDto> getUserByToken(@NotNull @NotEmpty String token);

    void sendEmailsForNewUsers();

    void sendResetPasswordLink(@NotNull @NotEmpty String email);

    void setPassword(@NotNull Long userId, @NotNull @NotEmpty String password);
}
