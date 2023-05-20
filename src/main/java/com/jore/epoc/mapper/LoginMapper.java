package com.jore.epoc.mapper;

import java.util.ArrayList;
import java.util.List;

import com.jore.epoc.bo.user.User;
import com.jore.epoc.dto.UserDto;

public interface LoginMapper {
    public LoginMapper INSTANCE = new LoginMapper() {
        @Override
        public User loginDtoToLogin(UserDto loginDto) {
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
        public List<UserDto> loginToLoginDto(Iterable<User> users) {
            List<UserDto> result = new ArrayList<>();
            users.forEach(user -> result.add(loginToLoginDto(user)));
            return result;
        }

        @Override
        public UserDto loginToLoginDto(User login) {
            return UserDto.builder().id(login.getId()).name(login.getName()).email(login.getEmail()).login(login.getLogin()).password(login.getPassword()).isAdmin(login.isAdmin()).build();
        }
    };

    public User loginDtoToLogin(UserDto loginDto);

    public List<UserDto> loginToLoginDto(Iterable<User> users);

    public UserDto loginToLoginDto(User login);
}
