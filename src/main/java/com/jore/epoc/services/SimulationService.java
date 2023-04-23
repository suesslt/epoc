package com.jore.epoc.services;

import java.util.List;
import java.util.Optional;

import com.jore.epoc.dto.AdjustCreditLineDto;
import com.jore.epoc.dto.BuildFactoryDto;
import com.jore.epoc.dto.BuildStorageDto;
import com.jore.epoc.dto.BuyRawMaterialDto;
import com.jore.epoc.dto.CompanySimulationStepDto;
import com.jore.epoc.dto.CompletedUserSimulationDto;
import com.jore.epoc.dto.EnterMarketDto;
import com.jore.epoc.dto.OpenUserSimulationDto;
import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.dto.SimulationStatisticsDto;

public interface SimulationService {
    void adjustCreditLine(Integer companySimulationStepId, AdjustCreditLineDto adjustCreditLineDto);

    void buildFactory(Integer companySimulationStepId, BuildFactoryDto buildFactoryDto);

    void buildStorage(Integer companySimulationStepId, BuildStorageDto buildStorageDto);

    void buyRawMaterial(Integer companySimulationStepId, BuyRawMaterialDto buyRawMaterialDto);

    void buySimulations(String user, int nrOfSimulations);

    Integer countAvailableSimulations(String user);

    void enterMarket(Integer companySimulationStepId, EnterMarketDto enterMarketDto);

    void finishMoveFor(Integer companySimulationStepId);

    List<CompletedUserSimulationDto> getCompletedSimulationsForUser(String user);

    Optional<CompanySimulationStepDto> getCurrentCompanySimulationStep(Integer companyId);

    Optional<SimulationDto> getNextAvailableSimulationForOwner(String user);

    List<OpenUserSimulationDto> getOpenSimulationsForUser(String user);

    SimulationStatisticsDto getSimulationStatistics(Integer simulationId);

    void updateSimulation(SimulationDto simulationDto);
}
