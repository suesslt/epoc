package com.jore.epoc.bo.message;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.extern.log4j.Log4j2;

// https://docs.aws.amazon.com/ses/latest/DeveloperGuide/send-using-smtp-java.html
@Log4j2
public class Mailbox {
    private static final String CONTENT_TEXT_PLAIN = "text/plain";
    private static final String AUTH = Boolean.toString(true);
    private static final String START_TLS = Boolean.toString(true);
    private static final String PROTOCOL = "smtp";
    private static final String FROM = "edmond.dantes@bluewin.ch";
    private static final String FROMNAME = "Edmond Dantès";
    private static final String TO = "edmond.dantes@bluewin.ch";
    private static final String SMTP_USERNAME = "edmond.dantes@bluewin.ch";
    private static final String SMTP_PASSWORD = "M4tter8orn";
    private static final String HOST = "smtpauths.bluewin.ch";
    private static final String PORT = "587";
    private static final Mailbox MAILBOX = new Mailbox();

    public static Mailbox getInstance() {
        return MAILBOX;
    }

    private Properties props = System.getProperties();
    private Session session;

    private Mailbox() {
        //        init(); TODO Refactor
    }

    synchronized public void send(String subject, String body) {
        //        Thread thread = new Thread(new Runnable() {
        //            @Override
        //            public void run() {
        //                internalSend(FROMNAME, FROM, TO, subject, body);
        //            }
        //        });
        //        thread.start();
    }

    private void init() {
        props.put("mail.transport.protocol", PROTOCOL);
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.starttls.enable", START_TLS);
        props.put("mail.smtp.auth", AUTH);
        session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });
    }

    private void internalSend(String senderName, String senderAddress, String receivers, String subject, String body) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(senderAddress, senderName));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receivers));
            msg.setSubject(subject);
            msg.setContent(body, CONTENT_TEXT_PLAIN);
            Transport transport = session.getTransport();
            try {
                Transport.send(msg);
                log.debug("Message sent.");
            } catch (Exception e) {
                log.warn(e);
            } finally {
                transport.close();
            }
        } catch (Exception e) {
            log.warn(e);
        }
    }
}
