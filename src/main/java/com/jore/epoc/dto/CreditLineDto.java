package com.jore.epoc.dto;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.orders.CreditEventDirection;
import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditLineDto implements DataTransferObject {
    private Integer id;
    private CreditEventDirection direction;
    private Money amount;
}
