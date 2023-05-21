package com.jore.epoc.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.Market;

public interface MarketRepository extends CrudRepository<Market, Integer> {
    Optional<Market> findByName(String name);
}
