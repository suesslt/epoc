package com.jore.epoc.services;

import java.util.List;
import java.util.Optional;

import com.jore.epoc.dto.CompanySimulationStepDto;
import com.jore.epoc.dto.CreditLineDto;
import com.jore.epoc.dto.FactoryOrderDto;
import com.jore.epoc.dto.MarketDto;
import com.jore.epoc.dto.OpenUserSimulationDto;
import com.jore.epoc.dto.RawMaterialDto;
import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.dto.StorageDto;

public interface SimulationService {
    void adjustCreditLine(Integer companySimulationStepId, CreditLineDto creditLineDto);

    void buildFactory(Integer companySimulationStepId, FactoryOrderDto factoryOrderDto);

    void buildStorage(Integer companySimulationStepId, StorageDto storageDto);

    void buyRawMaterials(Integer companySimulationStepId, RawMaterialDto rawMaterialDto);

    void buySimulations(String user, int nrOfSimulations);

    Integer countAvailableSimulations(String user);

    void distributeInMarket(Integer companySimulationStepId, MarketDto marketDto);

    void finishMoveFor(Integer companySimulationStepId);

    Optional<CompanySimulationStepDto> getCurrentCompanySimulationStep(Integer companyId);

    Optional<SimulationDto> getNextAvailableSimulationForOwner(String user);

    List<OpenUserSimulationDto> getOpenSimulationsForUser(String user);

    void updateSimulation(SimulationDto simulationDto);
}
