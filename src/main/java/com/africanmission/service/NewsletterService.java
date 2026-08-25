package com.africanmission.service;

import com.africanmission.model.Newsletter;
import com.africanmission.repository.NewsletterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsletterService {

    private final NewsletterRepository newsletterRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public Newsletter subscribe(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse e-mail ne peut pas être vide.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        Optional<Newsletter> existingSubscriber = newsletterRepository.findByEmailIgnoreCase(normalizedEmail);

        Newsletter subscriber;
        if (existingSubscriber.isPresent()) {
            subscriber = existingSubscriber.get();
            if (Boolean.TRUE.equals(subscriber.getIsActive())) {
                throw new IllegalStateException("Cet e-mail est déjà abonné à la newsletter.");
            }
            subscriber.setIsActive(true);
            subscriber = newsletterRepository.save(subscriber);
        } else {
            subscriber = new Newsletter();
            subscriber.setEmail(normalizedEmail);
            subscriber.setIsActive(true);
            subscriber = newsletterRepository.save(subscriber);
        }

        // Déclenchement de la notification pour le Back-Office
        notificationService.createNotification(
                "Nouvel abonné à la Newsletter",
                "L'adresse " + subscriber.getEmail() + " s'est abonnée avec succès.",
                "success",
                "/admin/newsletter"
        );

        return subscriber;
    }

    public void unsubscribe(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse e-mail ne peut pas être vide.");
        }

        Newsletter subscriber = newsletterRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Abonné introuvable avec cet e-mail."));

        subscriber.setIsActive(false);
        newsletterRepository.save(subscriber);
    }

    public void unsubscribeById(Long id) {
        Newsletter subscriber = getById(id);
        subscriber.setIsActive(false);
        newsletterRepository.save(subscriber);
    }

    public void toggleStatus(Long id) {
        Newsletter subscriber = getById(id);
        subscriber.setIsActive(!Boolean.TRUE.equals(subscriber.getIsActive()));
        newsletterRepository.save(subscriber);
    }

    public void deleteById(Long id) {
        if (!newsletterRepository.existsById(id)) {
            throw new IllegalArgumentException("Abonné introuvable avec l'ID : " + id);
        }
        newsletterRepository.deleteById(id);
    }

    public Newsletter save(Newsletter subscriber) {
        if (subscriber == null || subscriber.getEmail() == null || subscriber.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse e-mail ne peut pas être vide.");
        }
        subscriber.setEmail(subscriber.getEmail().trim().toLowerCase());
        return newsletterRepository.save(subscriber);
    }

    public void sendNewsletterToAll(String subject, String content) {
        List<Newsletter> activeSubscribers = getAllActiveSubscribers();
        for (Newsletter subscriber : activeSubscribers) {
            try {
                emailService.sendHtmlEmail(subscriber.getEmail(), subject, content);
            } catch (Exception e) {
                System.err.println("Échec d'envoi à : " + subscriber.getEmail() + " - " + e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public Newsletter getById(Long id) {
        return newsletterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Abonné introuvable avec l'ID : " + id));
    }

    @Transactional(readOnly = true)
    public List<Newsletter> getAllActiveSubscribers() {
        return newsletterRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Newsletter> getAllSubscribers() {
        return newsletterRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long getSubscriberCount() {
        return newsletterRepository.count();
    }
}