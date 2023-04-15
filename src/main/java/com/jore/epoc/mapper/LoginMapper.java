package com.jore.epoc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.jore.epoc.bo.Login;
import com.jore.epoc.dto.LoginDto;

@Mapper
public interface LoginMapper {
    public LoginMapper INSTANCE = Mappers.getMapper(LoginMapper.class);

    @Mapping(target = "admin", ignore = true)
    public Login loginDtoToLogin(LoginDto loginDto);

    public LoginDto loginToLoginDto(Login login);
}
