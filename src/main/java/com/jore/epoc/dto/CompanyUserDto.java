package com.jore.epoc.dto;

import com.jore.jpa.DataTransferObject;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyUserDto implements DataTransferObject {
    @NotNull
    private Long companyId;
    @NotNull
    @NotEmpty
    @Email
    private String email;

    @Override
    public String toString() {
        return email;
    }
}
