package com.jore.epoc.dto;

import java.util.ArrayList;
import java.util.List;

import com.jore.jpa.DataTransferObject;

import lombok.Data;

@Data
/**
 * This is the main DTO for the display of company step related information
 */
public class CompanySimulationStepDto implements DataTransferObject {
    private Integer id;
    private String companyName;
    private List<FactoryDto> factories = new ArrayList<>();
    private CreditLineDto creditLine;
    private List<StorageDto> storages = new ArrayList<>();
    private List<DistributionInMarketDto> distributionInMarkets = new ArrayList<>();
    private List<MarketDto> markets = new ArrayList<>();

    public void addDistributionInMarket(DistributionInMarketDto distributionInMarketDto) {
        distributionInMarkets.add(distributionInMarketDto);
    }

    public void addFactory(FactoryDto factoryDto) {
        factories.add(factoryDto);
    }

    public void addMarket(MarketDto marketDto) {
        markets.add(marketDto);
    }

    public void addStorage(StorageDto storageDto) {
        storages.add(storageDto);
    }
}
