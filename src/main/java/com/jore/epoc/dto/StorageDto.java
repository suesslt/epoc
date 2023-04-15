package com.jore.epoc.dto;

import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageDto implements DataTransferObject {
    private int capacity;
    private Integer id;
}
