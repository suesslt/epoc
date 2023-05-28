package com.jore.epoc.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.Simulation;

public interface SimulationRepository extends CrudRepository<Simulation, Long> {
    List<Simulation> findByIsStartedAndOwnerId(boolean isStarted, Long ownerId);

    List<Simulation> findByOwnerId(Long ownerId);
}
