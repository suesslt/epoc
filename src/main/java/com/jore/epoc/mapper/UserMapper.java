package com.jore.epoc.mapper;

import java.util.ArrayList;
import java.util.List;

import com.jore.epoc.bo.user.User;
import com.jore.epoc.dto.UserDto;

public interface UserMapper {
    public UserMapper INSTANCE = new UserMapper() {
        @Override
        public User userDtoToUser(UserDto userDto) {
            User result = new User();
            result.setId(userDto.getId());
            result.setFirstName(userDto.getFirstName());
            result.setLastName(userDto.getLastName());
            result.setEmail(userDto.getEmail());
            result.setUsername(userDto.getUsername());
            result.setPassword(userDto.getPassword());
            result.setAdmin(userDto.isAdmin());
            return result;
        }

        @Override
        public List<UserDto> userToUserDto(Iterable<User> users) {
            List<UserDto> result = new ArrayList<>();
            users.forEach(user -> result.add(userToUserDto(user)));
            return result;
        }

        @Override
        public UserDto userToUserDto(User user) {
            return UserDto.builder().id(user.getId()).firstName(user.getFirstName()).lastName(user.getLastName()).email(user.getEmail()).username(user.getUsername()).password(user.getPassword()).isAdmin(user.isAdmin()).roles(user.getRoles()).build();
        }
    };

    public User userDtoToUser(UserDto userDto);

    public List<UserDto> userToUserDto(Iterable<User> users);

    public UserDto userToUserDto(User user);
}
