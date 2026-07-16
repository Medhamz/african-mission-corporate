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
        logger.info("💾 Sauvegarde du message: {}", message.getMessage());
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessagesBySession(Long sessionId) {
        logger.info("📋 Récupération des messages pour la session ID: {}", sessionId);
        return chatMessageRepository.findBySessionIdOrderBySentAtAsc(sessionId);
    }

    public List<ChatMessage> getUnreadMessages(Long sessionId) {
        return chatMessageRepository.findBySessionIdAndIsReadFalse(sessionId);
    }

    public ChatMessage markAsRead(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));
        message.setIsRead(true);
        return chatMessageRepository.save(message);
    }

    public void markAllAsRead(Long sessionId) {
        List<ChatMessage> unread = getUnreadMessages(sessionId);
        if (!unread.isEmpty()) {
            unread.forEach(msg -> msg.setIsRead(true));
            chatMessageRepository.saveAll(unread);
            logger.info("✅ {} messages marqués comme lus", unread.size());
        }
    }

    public List<ChatMessage> getRecentMessages(Long sessionId, int limit) {
        return chatMessageRepository.findTop10BySessionIdOrderBySentAtDesc(sessionId);
    }
}