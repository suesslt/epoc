package com.jore.epoc.dto;

import com.jore.jpa.DataTransferObject;

import lombok.Data;

@Data
public class SimulationStatisticsDto implements DataTransferObject {
    private Integer totalSoldProducts;
}
