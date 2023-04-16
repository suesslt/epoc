package com.jore.epoc.mapper;

import com.jore.epoc.bo.Login;
import com.jore.epoc.dto.LoginDto;

public interface LoginMapper {
    public LoginMapper INSTANCE = new LoginMapper() {
        @Override
        public Login loginDtoToLogin(LoginDto loginDto) {
            Login result = new Login();
            result.setId(loginDto.getId());
            result.setName(loginDto.getName());
            result.setEmail(loginDto.getEmail());
            result.setLogin(loginDto.getLogin());
            result.setPassword(loginDto.getPassword());
            result.setAdmin(loginDto.isAdmin());
            return result;
        }

        @Override
        public LoginDto loginToLoginDto(Login login) {
            return LoginDto.builder().id(login.getId()).name(login.getName()).email(login.getEmail()).login(login.getLogin()).password(login.getPassword()).isAdmin(login.isAdmin()).build();
        }
    };

    public Login loginDtoToLogin(LoginDto loginDto);

    public LoginDto loginToLoginDto(Login login);
}
