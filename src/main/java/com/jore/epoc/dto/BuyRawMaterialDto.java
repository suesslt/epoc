package com.jore.epoc.dto;

import java.time.YearMonth;

import com.jore.jpa.DataTransferObject;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuyRawMaterialDto implements DataTransferObject {
    @NotNull
    private Long companyId;
    @Min(1)
    @Max(1000)
    @NotNull
    private Integer amount;
    @NotNull
    private YearMonth executionMonth;
}
