package com.africanmission.service;

import com.africanmission.model.Newsletter;
import com.africanmission.repository.NewsletterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final NewsletterRepository newsletterRepository;

    public Newsletter subscribe(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse e-mail ne peut pas être vide.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        Optional<Newsletter> existingSubscriber = newsletterRepository.findByEmailIgnoreCase(normalizedEmail);

        if (existingSubscriber.isPresent()) {
            Newsletter subscriber = existingSubscriber.get();
            if (Boolean.TRUE.equals(subscriber.getIsActive())) {
                throw new IllegalStateException("Cet e-mail est déjà abonné à la newsletter.");
            }
            // Réabonnement
            subscriber.setIsActive(true);
            return newsletterRepository.save(subscriber);
        }

        Newsletter subscriber = new Newsletter();
        subscriber.setEmail(normalizedEmail);
        subscriber.setIsActive(true);
        return newsletterRepository.save(subscriber);
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

    public List<Newsletter> getAllActiveSubscribers() {
        return newsletterRepository.findByIsActiveTrue();
    }

    public long getSubscriberCount() {
        return newsletterRepository.count();
    }
}