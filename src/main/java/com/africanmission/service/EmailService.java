package com.africanmission.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:sidimohamedhamza2@gmail.com}")
    private String mailFrom;

    public void sendContactConfirmation(String to, String name, String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(mailFrom);
            mail.setTo(to);
            mail.setSubject("Confirmation de votre message - African Mission Corporate");
            mail.setText("Bonjour " + name + ",\n\n" +
                    "Nous avons bien reçu votre message :\n\n" +
                    "\"" + message + "\"\n\n" +
                    "Nous vous répondrons dans les plus brefs délais.\n\n" +
                    "Cordialement,\n" +
                    "L'équipe African Mission Corporate");
            mailSender.send(mail);
            log.info("E-mail de confirmation envoyé avec succès à : {}", to);
        } catch (Exception e) {
            log.error("Échec d'envoi de la confirmation à {} : {}", to, e.getMessage(), e);
        }
    }

    public void sendAdminNotification(String name, String email, String subject, String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(mailFrom);
            mail.setTo("sidimohamedhamza2@gmail.com");
            mail.setSubject("Nouveau message de contact - " + subject);
            mail.setText("Nouveau message de :\n\n" +
                    "Nom : " + name + "\n" +
                    "Email : " + email + "\n" +
                    "Sujet : " + subject + "\n\n" +
                    "Message :\n" + message);
            mailSender.send(mail);
            log.info("Notification d'admin envoyée pour le message de : {}", email);
        } catch (Exception e) {
            log.error("Échec d'envoi de la notification admin : {}", e.getMessage(), e);
        }
    }

    public void sendHtmlEmail(String to, String subject, String content) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            log.info("E-mail HTML envoyé avec succès à : {}", to);
        } catch (Exception e) {
            log.error("Échec d'envoi de l'e-mail HTML à {} : {}", to, e.getMessage(), e);
            throw e;
        }
    }
}