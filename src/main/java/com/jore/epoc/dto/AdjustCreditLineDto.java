package com.jore.epoc.dto;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.CreditEventDirection;
import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdjustCreditLineDto implements DataTransferObject {
    private CreditEventDirection direction;
    private Money amount;
}
