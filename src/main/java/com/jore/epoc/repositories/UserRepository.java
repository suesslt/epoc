package com.jore.epoc.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.user.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    void deleteByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndPassword(String username, String password);
}
