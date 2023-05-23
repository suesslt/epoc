package com.jore.epoc.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jore.epoc.bo.user.Role;
import com.jore.jpa.DataTransferObject;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDto implements DataTransferObject {
    private Integer id;
    @NotEmpty
    @NotNull
    private String username;
    @JsonIgnore
    private String password;
    @NotEmpty
    @NotNull
    private String firstName;
    @NotEmpty
    @NotNull
    private String lastName;
    private String phone;
    @Email
    @NotEmpty
    @NotNull
    private String email;
    private boolean isAdmin;
    private Set<Role> roles;
}