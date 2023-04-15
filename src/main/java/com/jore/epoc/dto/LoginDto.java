package com.jore.epoc.dto;

import com.jore.jpa.DataTransferObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto implements DataTransferObject {
    private Integer id;;
    private String login;
    private String password;
    private String name;
    private String email;
}
