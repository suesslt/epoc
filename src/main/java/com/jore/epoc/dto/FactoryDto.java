package com.jore.epoc.dto;

import com.jore.jpa.DataTransferObject;

import lombok.Data;

@Data
public class FactoryDto implements DataTransferObject {
    private Long id;
    private Integer capacity;
}
