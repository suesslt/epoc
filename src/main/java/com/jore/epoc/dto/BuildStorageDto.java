package com.jore.epoc.dto;

import java.time.YearMonth;

import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuildStorageDto implements DataTransferObject {
    private Integer capacity;
    private YearMonth executionMonth;
}
