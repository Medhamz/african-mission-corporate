package com.africanmission.service;

import com.africanmission.model.ChatMessage;
import com.africanmission.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        message.setIsApproved(false);
        logger.info("💾 Sauvegarde du message de : {}", message.getUsername());
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getApprovedMessages() {
        logger.info("📋 Récupération des messages approuvés");
        return chatMessageRepository.findTop10ByIsApprovedTrueOrderByCreatedAtDesc();
    }

    public List<ChatMessage> getPendingMessages() {
        logger.info("⏳ Récupération des messages en attente");
        return chatMessageRepository.findByIsApprovedFalseOrderByCreatedAtAsc();
    }

    public ChatMessage approveMessage(Long id) {
        ChatMessage message = chatMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));
        message.setIsApproved(true);
        logger.info("✅ Message approuvé : {}", id);
        return chatMessageRepository.save(message);
    }

    public void deleteMessage(Long id) {
        logger.info("🗑️ Message supprimé : {}", id);
        chatMessageRepository.deleteById(id);
    }

    public long getPendingCount() {
        return chatMessageRepository.findByIsApprovedFalseOrderByCreatedAtAsc().size();
    }
}