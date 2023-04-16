package com.jore.epoc.dto;

import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginDto implements DataTransferObject {
    private Integer id;;
    private String login;
    private String password;
    private String name;
    private String email;
    private boolean isAdmin;
}
