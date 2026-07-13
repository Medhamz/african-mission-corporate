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

    public List<ContactMessage> getAllMessages() {
        return contactRepository.findAll();
    }

    public ContactMessage markAsRead(Long id) {
        ContactMessage message = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setIsRead(true);
        return contactRepository.save(message);
    }
}