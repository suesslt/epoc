package com.jore.epoc.mapper;

import com.jore.epoc.bo.user.User;
import com.jore.epoc.dto.LoginDto;

public interface LoginMapper {
    public LoginMapper INSTANCE = new LoginMapper() {
        @Override
        public User loginDtoToLogin(LoginDto loginDto) {
            User result = new User();
            result.setId(loginDto.getId());
            result.setName(loginDto.getName());
            result.setEmail(loginDto.getEmail());
            result.setLogin(loginDto.getLogin());
            result.setPassword(loginDto.getPassword());
            result.setAdmin(loginDto.isAdmin());
            return result;
        }

        @Override
        public LoginDto loginToLoginDto(User login) {
            return LoginDto.builder().id(login.getId()).name(login.getName()).email(login.getEmail()).login(login.getLogin()).password(login.getPassword()).isAdmin(login.isAdmin()).build();
        }
    };

    public User loginDtoToLogin(LoginDto loginDto);

    public LoginDto loginToLoginDto(User login);
}
