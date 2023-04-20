package com.jore.epoc.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.Market;
import com.jore.epoc.bo.MarketSimulation;
import com.jore.epoc.bo.Simulation;

public interface MarketSimulationRepository extends CrudRepository<MarketSimulation, Integer> {
    Optional<MarketSimulation> findByMarketAndSimulation(Market market, Simulation simulation);
}
