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

    public ContactMessage saveMessage(ContactMessage message) {
        return contactRepository.save(message);
    }

    public List<ContactMessage> getAllUnreadMessages() {
        return contactRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    // ✅ Méthode ajoutée pour récupérer TOUS les messages
    public List<ContactMessage> getAllMessages() {
        return contactRepository.findAll();
    }

    public ContactMessage markAsRead(Long id) {
        ContactMessage message = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));
        message.setIsRead(true);
        return contactRepository.save(message);
    }

    // ✅ Méthode ajoutée pour supprimer un message
    public void deleteMessage(Long id) {
        contactRepository.deleteById(id);
    }

    public ContactMessage getMessageById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));
    }
}