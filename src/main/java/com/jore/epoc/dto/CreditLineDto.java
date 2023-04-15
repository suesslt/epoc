package com.jore.epoc.dto;

import java.util.ArrayList;
import java.util.List;

import com.jore.jpa.DataTransferObject;

import lombok.Data;

@Data
public class CreditLineDto implements DataTransferObject {
    private Integer id;
    private List<CreditLineEventDto> events = new ArrayList<>();

    public void addCreditLineEvent(CreditLineEventDto event) {
        events.add(event);
    }
}
