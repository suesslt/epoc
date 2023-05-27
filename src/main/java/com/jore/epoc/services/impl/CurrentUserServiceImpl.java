package com.jore.epoc.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jore.epoc.bo.user.User;
import com.jore.epoc.dto.UserDto;
import com.jore.epoc.mapper.UserMapper;
import com.jore.epoc.repositories.UserRepository;
import com.jore.epoc.services.CurrentUserService;

// TODO clear currentUser after logout
@Service
public class CurrentUserServiceImpl implements CurrentUserService {
    //    private static Optional<Authentication> getAuthentication() {
    //        return Optional.of(SecurityContextHolder.getContext()).map(SecurityContext::getAuthentication).filter(auth -> !(auth instanceof AnonymousAuthenticationToken));
    //    }
    private static List<GrantedAuthority> getAuthorities(User user) {
        List<GrantedAuthority> result = new ArrayList<>();
        result.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (user.isAdmin()) {
            result.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return result;
    }

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createAdmin(UserDto adminDto) {
        User login = UserMapper.INSTANCE.userDtoToUser(adminDto);
        login.setAdmin(true);
        if (userRepository.findByUsername(adminDto.getUsername()).isPresent()) {
            throw new IllegalStateException();
        }
        return UserMapper.INSTANCE.userToUserDto(userRepository.save(login));
    }

    @Override
    public Optional<UserDto> getAuthenticatedUser() {
        Optional<UserDto> result = Optional.empty();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            result = userRepository.findByUsername(authentication.getName()).map(user -> UserMapper.INSTANCE.userToUserDto(user));
        }
        return result;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), getAuthorities(user.get()));
    }
}
