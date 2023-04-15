package com.jore.epoc.dto;

import java.util.ArrayList;
import java.util.List;

import com.jore.jpa.DataTransferObject;

import lombok.Data;

@Data
public class CompanySimulationStepDto implements DataTransferObject {
    private Integer id;
    private String companyName;
    private List<FactoryDto> factories = new ArrayList<>();
    private List<CreditLineDto> creditLines = new ArrayList<>();
    private List<StorageDto> storages = new ArrayList<>();
    private List<DistributionInMarketDto> distributionInMarkets = new ArrayList<>();

    public void addCreditLine(CreditLineDto creditLineDto) {
        creditLines.add(creditLineDto);
    }

    public void addDistributionInMarket(DistributionInMarketDto distributionInMarketDto) {
        distributionInMarkets.add(distributionInMarketDto);
    }

    public void addFactory(FactoryDto factoryDto) {
        factories.add(factoryDto);
    }

    public void addStorage(StorageDto storageDto) {
        storages.add(storageDto);
    }
}
