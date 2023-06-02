package com.jore.epoc.dto;

import java.util.ArrayList;
import java.util.List;

import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CompanyDto implements DataTransferObject {
    private Long id;
    private Long simulationId;
    private String name;
    @Builder.Default
    private List<UserDto> users = new ArrayList<>();
    private final List<CreditLineDto> creditLines = new ArrayList<>();
    private final List<FactoryDto> factories = new ArrayList<>();
    private final List<StorageDto> storages = new ArrayList<>();
    private final List<String> emails = new ArrayList<>();

    public void addCreditLine(CreditLineDto creditLine) {
        creditLines.add(creditLine);
    }

    public void addFactory(FactoryDto factoryDto) {
        factories.add(factoryDto);
    }

    public void addStorage(StorageDto storageDto) {
        storages.add(storageDto);
    }

    public void addUserEmail(String email) {
        emails.add(email);
    }
}
