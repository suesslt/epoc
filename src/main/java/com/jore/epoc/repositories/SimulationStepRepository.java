package com.jore.epoc.repositories;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.step.SimulationStep;

public interface SimulationStepRepository extends CrudRepository<SimulationStep, Integer> {
}
