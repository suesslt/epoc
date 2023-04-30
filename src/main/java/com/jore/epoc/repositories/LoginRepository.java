package com.jore.epoc.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.user.User;

public interface LoginRepository extends CrudRepository<User, Integer> {
    void deleteByLogin(String login);

    Optional<User> findByLogin(String userLogin);

    Optional<User> findByLoginAndPassword(String login, String password);
}
