package com.jore.epoc.services;

import java.util.List;
import java.util.Optional;

import com.jore.epoc.dto.AdjustCreditLineDto;
import com.jore.epoc.dto.BuildFactoryDto;
import com.jore.epoc.dto.BuildStorageDto;
import com.jore.epoc.dto.BuyRawMaterialDto;
import com.jore.epoc.dto.CompanyDto;
import com.jore.epoc.dto.CompanySimulationStepDto;
import com.jore.epoc.dto.CompanyUserDto;
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
import jakarta.validation.constraints.NotNull;

public interface SimulationService {
    void buildFactory(@Valid BuildFactoryDto buildFactoryDto);

    void buildStorage(@Valid BuildStorageDto buildStorageDto);

    void buyRawMaterial(@Valid BuyRawMaterialDto buyRawMaterialDto);

    void buySimulations(@Min(1) int nrOfSimulations, @NotNull Long ownerId);

    void decreaseCreditLine(@Valid AdjustCreditLineDto decreaseCreditLineDto);

    void deleteCompany(@Valid CompanyDto company);

    void deleteCompanyUser(@Valid CompanyUserDto companyUser);

    void enterMarket(@Valid EnterMarketDto enterMarketDto);

    void finishMoveFor(@NotNull Long companyId);

    List<CompletedUserSimulationDto> getCompletedSimulationsForUser(@NotNull Long userId);

    Optional<CompanySimulationStepDto> getCurrentCompanySimulationStep(@NotNull Long companyId);

    Optional<SimulationDto> getNextAvailableSimulationForOwner(@NotNull Long userId);

    List<OpenUserSimulationDto> getOpenSimulationsForUser(@NotNull Long userId);

    List<SimulationDto> getSimulationsForOwner(@NotNull Long ownerId);

    SimulationStatisticsDto getSimulationStatistics(@NotNull Long simulationId);

    void increaseCreditLine(@Valid AdjustCreditLineDto increaseCreditLineDto);

    void increaseProductivity(@Valid IncreaseProductivityDto increaseProductivityDto);

    void increaseQuality(@Valid IncreaseQualityDto increaseQualityDto);

    void runMarketingCampaign(@Valid RunMarketingCampaignDto runMarketingCampaignDto);

    CompanyDto saveCompany(CompanyDto company);

    void saveCompanyUser(@Valid CompanyUserDto companyUserDto);

    void saveSimulation(@Valid SimulationDto simulation);

    void setIntentedSalesAndPrice(@Valid IntendedSalesAndPriceDto intentendSalesAndPriceDto);

    void updateSimulation(@Valid SimulationDto simulationDto);
}
