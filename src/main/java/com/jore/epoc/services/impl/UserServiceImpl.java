package com.jore.epoc.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.jore.epoc.bo.message.Messages;
import com.jore.epoc.bo.user.User;
import com.jore.epoc.bo.user.UserInCompanyRole;
import com.jore.epoc.dto.UserDto;
import com.jore.epoc.mapper.UserMapper;
import com.jore.epoc.repositories.UserInCompanyRoleRepository;
import com.jore.epoc.repositories.UserRepository;
import com.jore.epoc.services.UserService;
import com.jore.mail.Mail;
import com.jore.util.Util;

import jakarta.validation.Valid;

@Component
@Validated
@Service
public class UserServiceImpl implements UserService {
    private static final String ADMIN_EPOC_CH = "admin@epoc.ch";
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserInCompanyRoleRepository userInCompanyRoleRepository;

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
        return userRepository.findById(id.intValue()).map(user -> UserMapper.INSTANCE.userToUserDto(user));
    }

    @Override
    public Optional<UserDto> getByUsername(String username) {
        return userRepository.findByUsername(username).map(user -> UserMapper.INSTANCE.userToUserDto(user));
    }

    @Override
    @Transactional
    public Collection<Mail> getEmailsForNewUsers() {
        List<Mail> result = new ArrayList<>();
        Iterable<UserInCompanyRole> findByIsInvitationRequired = userInCompanyRoleRepository.findByIsInvitationRequired(true);
        for (UserInCompanyRole userInCompany : findByIsInvitationRequired) {
            userInCompany.setIsInvitationRequired(false);
            userInCompanyRoleRepository.save(userInCompany);
            Mail mail = new Mail();
            mail.setSender(ADMIN_EPOC_CH);
            mail.addToRecipient(userInCompany.getUser().getEmail());
            mail.addCcRecipient(userInCompany.getCompany().getSimulation().getOwner().getEmail());
            mail.setMessageBody(Messages.getMessage("mailSimulationReadyBody", userInCompany.getUser().getPassword()));
            mail.setSubject(Messages.getMessage("mailSimulationReadySubject", userInCompany.getCompany().getSimulation().getName()));
            result.add(mail);
        }
        return result;
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
            Optional<User> otherLogin = userRepository.findById(userDto.getId());
            if (otherLogin.isPresent()) {
                if (!otherLogin.get().getUsername().equals(userDto.getUsername())) {
                    Optional<User> existingLogin = userRepository.findByUsername(userDto.getUsername());
                    if (existingLogin.isEmpty()) {
                        User login = UserMapper.INSTANCE.userDtoToUser(userDto);
                        result = UserMapper.INSTANCE.userToUserDto(userRepository.save(login));
                    } else {
                        throw new IllegalStateException(errorData);
                    }
                } else {
                    User login = UserMapper.INSTANCE.userDtoToUser(userDto);
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
