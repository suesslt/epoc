package com.jore.epoc.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.Simulation;

public interface SimulationRepository extends CrudRepository<Simulation, Integer> {
    List<Simulation> findByIsStartedAndUserLogin(boolean isStarted, String login);
}
