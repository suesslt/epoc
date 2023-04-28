package com.jore.epoc.dto;

import java.time.YearMonth;

import com.jore.datatypes.money.Money;
import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdjustCreditLineDto implements DataTransferObject {
    private Money amount;
    private YearMonth executionMonth;
}
