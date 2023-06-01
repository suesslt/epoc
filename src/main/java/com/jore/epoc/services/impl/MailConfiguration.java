package com.jore.epoc.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfiguration {
    @Value("${mail.host}")
    private String host;
    @Value("${mail.port}")
    private int port;
    @Value("${mail.smtp.user}")
    private String username;
    @Value("${mail.smtp.password}")
    private String password;
    @Value("${mail.protocol}")
    private String protocol;
    @Value("${mail.sender}")
    private String from;
    @Value("${mail.encoding}")
    private String encoding;

    @Bean
    public MailSender mailSender() {
        JavaMailSenderImpl result = new JavaMailSenderImpl();
        result.setHost(host);
        result.setPort(port);
        result.setUsername(username);
        result.setPassword(password);
        result.setProtocol(protocol);
        result.setDefaultEncoding(encoding);
        //        result.setDefaultFileTypeMap(null);
        return result;
    }

    @Bean
    public SimpleMailMessage simpleMailMessage() {
        SimpleMailMessage result = new SimpleMailMessage();
        result.setFrom(from);
        return result;
    }
}
