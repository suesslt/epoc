package com.jore.epoc.dto;

import com.jore.jpa.DataTransferObject;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginDto implements DataTransferObject {
    //    @NotNull // TODO Steuerung über Kontext
    private Integer id;
    @NotEmpty
    private String login;
    @NotEmpty
    private String password;
    @NotEmpty
    private String name;
    @NotNull
    @Email
    private String email;
    private boolean isAdmin;
}
