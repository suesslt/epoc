package com.jore.epoc.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.jore.epoc.bo.message.Messages;
import com.jore.epoc.bo.user.User;
import com.jore.epoc.bo.user.UserInCompanyRole;
import com.jore.epoc.bo.user.UserToken;
import com.jore.epoc.dto.UserDto;
import com.jore.epoc.dto.UserTokenDto;
import com.jore.epoc.mapper.UserMapper;
import com.jore.epoc.repositories.UserInCompanyRoleRepository;
import com.jore.epoc.repositories.UserRepository;
import com.jore.epoc.repositories.UserTokenRepository;
import com.jore.epoc.services.UserAdminService;

import lombok.extern.log4j.Log4j2;

@Component
@Validated
@Service
@Log4j2
public class UserAdminServiceImpl implements UserAdminService {
    private static final String MAIL_SIMULATION_READY_BODY = "mailSimulationReadyBody";
    private static final String MAIL_SIMULATION_READY_SUBJECT = "mailSimulationReadySubject";
    private static final String SET_PASSWORD = "set_password/";
    private static final String HOST = "http://localhost:2569/";

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
    @Autowired
    private UserTokenRepository userTokenRepository;
    @Autowired
    private UserInCompanyRoleRepository userInCompanyRoleRepository;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private SimpleMailMessage templateMessage;

    @Override
    @Transactional
    public void deleteUserToken(Long userTokenId) {
        userTokenRepository.deleteById(userTokenId);
    }

    @Override
    @Transactional
    public Optional<UserDto> getAuthenticatedUser() {
        Optional<UserDto> result = Optional.empty();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            result = userRepository.findByUsername(authentication.getName()).map(user -> UserMapper.INSTANCE.userToUserDto(user));
        }
        return result;
    }

    @Override
    @Transactional
    public Optional<UserTokenDto> getUserByToken(String token) {
        Optional<UserTokenDto> result = Optional.empty();
        Optional<UserToken> userToken = userTokenRepository.findByToken(token);
        if (userToken.isPresent() && !userToken.get().isExpired()) {
            UserTokenDto userTokenDto = UserTokenDto.builder().userId(userToken.get().getUser().getId()).userTokenId(userToken.get().getId()).build();
            result = Optional.of(userTokenDto);
        }
        return result;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), getAuthorities(user.get()));
    }

    @Override
    @Transactional
    public void sendEmailsForNewUsers() {
        Iterable<UserInCompanyRole> findByIsInvitationRequired = userInCompanyRoleRepository.findByIsInvitationRequired(true);
        for (UserInCompanyRole userInCompany : findByIsInvitationRequired) {
            userInCompany.setIsInvitationRequired(false);
            userInCompanyRoleRepository.save(userInCompany);
            saveTokenAndSendMail(userInCompany.getUser(), Messages.getMessage(MAIL_SIMULATION_READY_SUBJECT, userInCompany.getCompany().getSimulation().getName()));
        }
    }

    @Override
    @Transactional
    public void sendResetPasswordLink(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            saveTokenAndSendMail(user.get(), "");
        } // if user not found do nothing..
    }

    @Override
    @Transactional
    public void setPassword(Long userId, String password) {
        User user = userRepository.findById(userId).get();
        user.setPassword(password);
        userRepository.save(user);
    }

    private void saveTokenAndSendMail(User user, String message) {
        UserToken userToken = new UserToken();
        String token = UUID.randomUUID().toString();
        userToken.setToken(token);
        userToken.setUser(user);
        userToken.calculateAndSetExpiryDate();
        log.debug("Create User Token: " + userToken);
        userTokenRepository.save(userToken);
        String url = HOST + SET_PASSWORD + token;
        sendMail(user.getEmail(), Messages.getMessage(MAIL_SIMULATION_READY_BODY, url), message, null);
    }

    private void sendMail(String mailTo, String mailText, String mailSubject, String mailCc) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleMailMessage mail = new SimpleMailMessage(templateMessage);
                mail.setTo("thommy.suessli@bluewin.ch"); // For testing only
                mail.setText(mailText);
                mail.setSubject(mailSubject);
                log.debug("Send mail: " + mail);
                try {
                    mailSender.send(mail);
                } catch (MailException e) {
                    log.error(e);
                }
            }
        });
        thread.start();
    }
}
