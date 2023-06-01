package com.jore.epoc.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.user.UserToken;

public interface UserTokenRepository extends CrudRepository<UserToken, Long> {
    Optional<UserToken> findByToken(String token);
}
