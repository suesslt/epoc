package com.jore.epoc.dto;

import com.jore.jpa.DataTransferObject;

import lombok.Data;

@Data
public class CompletedUserSimulationDto implements DataTransferObject {
    private Integer simulationId;
    private String simulationName;
    private String companyName;
    private Integer companyId;
}
