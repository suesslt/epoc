package com.jore.epoc.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jore.epoc.bo.user.User;
import com.jore.epoc.bo.user.UserInCompanyRole;
import com.jore.epoc.dto.UserDto;

import jakarta.validation.Valid;

public interface UserMapper {
    public UserMapper INSTANCE = new UserMapper() {
        @Override
        public User userDtoToUser(User user, @Valid UserDto userDto) {
            updateUser(userDto, user);
            return user;
        }

        @Override
        public User userDtoToUser(UserDto userDto) {
            User result = new User();
            result.setId(userDto.getId());
            updateUser(userDto, result);
            return result;
        }

        @Override
        public List<UserDto> userToUserDto(Iterable<User> users) {
            List<UserDto> result = new ArrayList<>();
            users.forEach(user -> result.add(userToUserDto(user)));
            return result;
        }

        @Override
        public List<UserDto> userToUserDto(List<UserInCompanyRole> users) {
            return users.stream().map(user -> userToUserDto(user)).collect(Collectors.toList());
        }

        @Override
        public UserDto userToUserDto(User user) {
            return UserDto.builder().id(user.getId()).firstName(user.getFirstName()).lastName(user.getLastName()).email(user.getEmail()).username(user.getUsername()).administrator(user.isAdmin()).phone(user.getPhone()).build();
        }

        public UserDto userToUserDto(UserInCompanyRole user) {
            return userToUserDto(user.getUser());
        }

        private void updateUser(UserDto userDto, User result) {
            result.setFirstName(userDto.getFirstName());
            result.setLastName(userDto.getLastName());
            result.setEmail(userDto.getEmail());
            result.setUsername(userDto.getUsername());
            result.setPhone(userDto.getPhone());
            result.setAdmin(userDto.isAdministrator());
        }
    };

    public User userDtoToUser(User user, @Valid UserDto userDto);

    public User userDtoToUser(UserDto userDto);

    public List<UserDto> userToUserDto(Iterable<User> users);

    public List<UserDto> userToUserDto(List<UserInCompanyRole> users);

    public UserDto userToUserDto(User user);
}
