package com.africanmission.service;

import com.africanmission.model.ContactMessage;
import com.africanmission.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final NotificationService notificationService;

    public ContactMessage saveMessage(ContactMessage message) {
        ContactMessage savedMessage = contactRepository.save(message);

        // Déclenchement de la notification pour le Back-Office
        notificationService.createNotification(
                "Nouveau message de contact",
                "Message de " + savedMessage.getName() + " (" + savedMessage.getEmail() + ") : " +
                        (savedMessage.getSubject() != null ? savedMessage.getSubject() : "Sans sujet"),
                "info",
                "/admin/messages"
        );

        return savedMessage;
    }

    public List<ContactMessage> getAllUnreadMessages() {
        return contactRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    public List<ContactMessage> getAllMessages() {
        return contactRepository.findAll();
    }

    public ContactMessage markAsRead(Long id) {
        ContactMessage message = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));
        message.setIsRead(true);
        return contactRepository.save(message);
    }

    public void deleteMessage(Long id) {
        contactRepository.deleteById(id);
    }

    public ContactMessage getMessageById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));
    }
}