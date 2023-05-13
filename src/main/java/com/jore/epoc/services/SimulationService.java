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
import com.jore.epoc.dto.IncreaseProductivityDto;
import com.jore.epoc.dto.IncreaseQualityDto;
import com.jore.epoc.dto.IntendedSalesAndPriceDto;
import com.jore.epoc.dto.OpenUserSimulationDto;
import com.jore.epoc.dto.RunMarketingCampaignDto;
import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.dto.SimulationStatisticsDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public interface SimulationService {
    void buildFactory(@Valid BuildFactoryDto buildFactoryDto);

    void buildStorage(@Valid BuildStorageDto buildStorageDto);

    void buyRawMaterial(@Valid BuyRawMaterialDto buyRawMaterialDto);

    void buySimulations(@Min(1) int nrOfSimulations);

    Integer countAvailableSimulations(@NotEmpty String user); // TODO Consider to delete, only used for test

    void decreaseCreditLine(@Valid AdjustCreditLineDto decreaseCreditLineDto);

    void enterMarket(@Valid EnterMarketDto enterMarketDto);

    void finishMoveFor(@NotNull Integer companySimulationStepId);

    List<CompletedUserSimulationDto> getCompletedSimulationsForUser(@NotEmpty String user);

    Optional<CompanySimulationStepDto> getCurrentCompanySimulationStep(@NotNull Integer companyId);

    Optional<SimulationDto> getNextAvailableSimulationForOwner();

    List<OpenUserSimulationDto> getOpenSimulationsForUser();

    SimulationStatisticsDto getSimulationStatistics(@NotNull Integer simulationId);

    void increaseCreditLine(@Valid AdjustCreditLineDto increaseCreditLineDto);

    void increaseProductivity(@Valid IncreaseProductivityDto increaseProductivityDto);

    void increaseQuality(@Valid IncreaseQualityDto increaseQualityDto);

    void runMarketingCampaign(@Valid RunMarketingCampaignDto runMarketingCampaignDto);

    void setIntentedSalesAndPrice(@Valid IntendedSalesAndPriceDto intentendSalesAndPriceDto);

    void updateSimulation(@Valid SimulationDto simulationDto);
}
