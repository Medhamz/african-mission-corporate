package com.africanmission.service;

import com.africanmission.model.ChatMessage;
import com.africanmission.model.ChatSession;
import com.africanmission.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessagesBySession(Long sessionId) {
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
        unread.forEach(msg -> msg.setIsRead(true));
        chatMessageRepository.saveAll(unread);
    }

    public List<ChatMessage> getRecentMessages(Long sessionId, int limit) {
        return chatMessageRepository.findTop10BySessionIdOrderBySentAtDesc(sessionId);
    }
}