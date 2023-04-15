package com.jore.epoc.dto;

import com.jore.jpa.DataTransferObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenUserSimulationDto implements DataTransferObject {
    private Integer simulationId;
    private Integer companyId;
    private String simulationName;
    private String companyName;
}
