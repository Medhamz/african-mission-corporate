package com.africanmission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendContactConfirmation(String to, String name, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject("Confirmation de votre message - African Mission Corporate");
        mail.setText("Bonjour " + name + ",\n\n" +
                "Nous avons bien reçu votre message :\n\n" +
                "\"" + message + "\"\n\n" +
                "Nous vous répondrons dans les plus brefs délais.\n\n" +
                "Cordialement,\n" +
                "L'équipe African Mission Corporate");
        mailSender.send(mail);
    }

    public void sendAdminNotification(String name, String email, String subject, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo("admin@africanmission.com");
        mail.setSubject("Nouveau message de contact - " + subject);
        mail.setText("Nouveau message de :\n\n" +
                "Nom : " + name + "\n" +
                "Email : " + email + "\n" +
                "Sujet : " + subject + "\n\n" +
                "Message :\n" + message);
        mailSender.send(mail);
    }
}