package com.jore.epoc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.jore.mail.Mail;
import com.jore.mail.MailReceiver;
import com.jore.mail.service.SendMailService;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class StubSendMailServiceImpl implements SendMailService {
    private Map<String, String> passwords = new HashMap<>();

    public void clear() {
        passwords.clear();
    }

    public String getPassword(String user) {
        return passwords.get(user);
    }

    @Override
    public void send(File file, String topic, String subject, String body, String sender) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void send(Iterable<Mail> mails) {
        for (Mail mail : mails) {
            passwords.put(mail.getToRecipients().get(0), mail.getMessageBody().substring(31));
            log.info(mail);
        }
    }

    @Override
    public void send(MailReceiver[] mailReceivers, String subject, String messageBody, File attachment, String sender) {
        throw new UnsupportedOperationException();
    }
}
