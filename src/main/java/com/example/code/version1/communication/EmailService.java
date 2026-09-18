package com.example.code.version1.communication;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;


import  com.example.configservice.*  ;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    private static final String FROM_EMAIL =
     ConfigService.getSecurity ( "mail.id") ;

    private static final String APP_PASSWORD = ConfigService.getSecurity ( "mail.secretkey") ;

    public static void sendOtp(
            String recipient,
            String otp)
            throws MessagingException {

        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session =
                Session.getInstance(
                        props,
                        new Authenticator() {

                            @Override
                            protected PasswordAuthentication
                            getPasswordAuthentication() {

                                return new PasswordAuthentication(
                                        FROM_EMAIL,
                                        APP_PASSWORD
                                );
                            }
                        });

        Message message =
                new MimeMessage(session);

        message.setFrom(
                new InternetAddress(FROM_EMAIL));

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(recipient));

        message.setSubject("Your Login OTP");

        message.setText(
                "Your OTP is: " + otp +
                "\n\nThis OTP will expire in 1 minute."
        );

        Transport.send(message);
    }
}

