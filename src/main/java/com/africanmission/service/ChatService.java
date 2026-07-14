package com.africanmission.service;

import com.africanmission.model.ChatMessage;
import com.africanmission.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        message.setIsApproved(false); // En attente de modération
        return chatMessageRepository.save(message);
    }

    // ✅ Méthode pour récupérer les messages approuvés
    public List<ChatMessage> getApprovedMessages() {
        return chatMessageRepository.findTop10ByIsApprovedTrueOrderByCreatedAtDesc();
    }

    // ✅ Méthode pour récupérer les messages en attente
    public List<ChatMessage> getPendingMessages() {
        return chatMessageRepository.findByIsApprovedFalseOrderByCreatedAtAsc();
    }

    public ChatMessage approveMessage(Long id) {
        ChatMessage message = chatMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));
        message.setIsApproved(true);
        return chatMessageRepository.save(message);
    }

    public void deleteMessage(Long id) {
        chatMessageRepository.deleteById(id);
    }

    // ✅ Méthode pour compter les messages en attente
    public long getPendingCount() {
        return chatMessageRepository.findByIsApprovedFalseOrderByCreatedAtAsc().size();
    }
}