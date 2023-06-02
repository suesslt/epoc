package com.jore.epoc.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.jore.epoc.bo.user.User;
import com.jore.epoc.dto.UserDto;
import com.jore.epoc.mapper.UserMapper;
import com.jore.epoc.repositories.UserRepository;
import com.jore.epoc.services.UserService;
import com.jore.util.Util;

import jakarta.validation.Valid;

@Component
@Validated
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public void delete(UserDto user) {
        userRepository.delete(UserMapper.INSTANCE.userDtoToUser(user));
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        userRepository.delete(userRepository.findByUsername(username).get());
    }

    @Override
    public List<UserDto> getAllFiltered(String filterText) {
        return getAllUsers().stream().filter(user -> matchesFilter(user, filterText)).collect(Collectors.toList());
    }

    public List<UserDto> getAllUsers() {
        return UserMapper.INSTANCE.userToUserDto(userRepository.findAll());
    }

    @Override
    public Optional<UserDto> getById(Long id) {
        return userRepository.findById(id).map(user -> UserMapper.INSTANCE.userToUserDto(user));
    }

    @Override
    public Optional<UserDto> getByUsername(String username) {
        return userRepository.findByUsername(username).map(user -> UserMapper.INSTANCE.userToUserDto(user));
    }

    @Override
    @Transactional
    public UserDto saveUser(@Valid UserDto userDto) {
        String errorData = "{error}";
        UserDto result = null;
        if (userDto.getId() == null) {
            Optional<User> existingLogin = userRepository.findByUsername(userDto.getUsername());
            if (existingLogin.isEmpty()) {
                User login = UserMapper.INSTANCE.userDtoToUser(userDto);
                result = UserMapper.INSTANCE.userToUserDto(userRepository.save(login));
            } else {
                throw new IllegalStateException(errorData);
            }
        } else {
            Optional<User> existing = userRepository.findById(userDto.getId());
            if (existing.isPresent()) {
                if (!existing.get().getUsername().equals(userDto.getUsername())) {
                    Optional<User> existingUsername = userRepository.findByUsername(userDto.getUsername());
                    if (existingUsername.isEmpty()) {
                        User login = UserMapper.INSTANCE.userDtoToUser(existing.get(), userDto);
                        result = UserMapper.INSTANCE.userToUserDto(userRepository.save(login));
                    } else {
                        throw new IllegalStateException(errorData);
                    }
                } else {
                    User login = UserMapper.INSTANCE.userDtoToUser(existing.get(), userDto);
                    result = UserMapper.INSTANCE.userToUserDto(userRepository.save(login));
                }
            } else {
                throw new IllegalStateException(errorData);
            }
        }
        return result;
    }

    private boolean matchesFilter(UserDto user, String filterText) {
        boolean result = false;
        result = result || Util.contains(user.getEmail(), filterText);
        result = result || Util.contains(user.getFirstName(), filterText);
        result = result || Util.contains(user.getLastName(), filterText);
        result = result || Util.contains(user.getUsername(), filterText);
        result = result || Util.contains(user.getPhone(), filterText);
        return result;
    }
}
