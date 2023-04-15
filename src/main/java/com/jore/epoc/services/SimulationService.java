package com.jore.epoc.services;

import java.util.List;
import java.util.Optional;

import com.jore.epoc.dto.CompanySimulationStepDto;
import com.jore.epoc.dto.FactoryOrderDto;
import com.jore.epoc.dto.OpenUserSimulationDto;
import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.dto.StorageDto;

public interface SimulationService {
    void buildFactory(Integer companySimulationId, FactoryOrderDto factoryOrderDto);

    void buildStorage(Integer companySimulationId, StorageDto storageDto);

    void buySimulations(String user, int nrOfSimulations);

    Integer countAvailableSimulations(String user);

    void finishMoveFor(Integer companySimulationId);

    Optional<CompanySimulationStepDto> getCurrentCompanySimulationStep(Integer companyId);

    SimulationDto getNextAvailableSimulationForOwner(String user);

    List<OpenUserSimulationDto> getOpenSimulationsForUser(String string);

    void updateSimulation(SimulationDto simulationDto);
}
