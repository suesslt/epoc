package com.jore.epoc.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.Login;

public interface LoginRepository extends CrudRepository<Login, Integer> {
    void deleteByLogin(String login);

    Optional<Login> findByLogin(String userLogin);

    Optional<Login> findByLoginAndPassword(String login, String password);
}
