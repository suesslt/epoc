package com.jore.epoc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jore.jpa.DataTransferObject;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDto implements DataTransferObject {
    private Integer id;
    @NotEmpty
    private String username;
    @JsonIgnore
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    @Email
    @NotEmpty
    private String email;
    private boolean isAdmin;
    private String roles;
}
