package com.jore.epoc.dto;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.CreditEventDirection;
import com.jore.jpa.DataTransferObject;

import lombok.Builder;

@Builder
public class CreditLineEventDto implements DataTransferObject {
    private CreditEventDirection direction;
    private Money amount;
}
