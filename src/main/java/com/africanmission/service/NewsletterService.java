package com.africanmission.service;

import com.africanmission.model.Newsletter;
import com.africanmission.repository.NewsletterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final NewsletterRepository newsletterRepository;

    public Newsletter subscribe(String email) {
        // Vérifier si l'email existe déjà
        if (newsletterRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Cet email est déjà abonné !");
        }

        Newsletter subscriber = new Newsletter();
        subscriber.setEmail(email);
        subscriber.setIsActive(true);
        return newsletterRepository.save(subscriber);
    }

    public void unsubscribe(String email) {
        Newsletter subscriber = newsletterRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email non trouvé"));
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