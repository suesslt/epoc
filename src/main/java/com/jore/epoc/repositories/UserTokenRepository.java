package com.jore.epoc.repositories;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.user.UserToken;

public interface UserTokenRepository extends CrudRepository<UserToken, Long> {
}
