package com.africanmission.service;

import com.africanmission.model.ChatSession;
import com.africanmission.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;

    public ChatSession createSession(String ipAddress) {
        ChatSession session = new ChatSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setIpAddress(ipAddress);
        session.setIsActive(true);
        session.setLastActivity(LocalDateTime.now());
        return chatSessionRepository.save(session);
    }

    public ChatSession getOrCreateSession(String sessionId, String ipAddress) {
        return chatSessionRepository.findBySessionId(sessionId)
                .orElseGet(() -> createSession(ipAddress));
    }

    public ChatSession getSessionById(String sessionId) {
        return chatSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session non trouvée"));
    }

    public List<ChatSession> getActiveSessions() {
        return chatSessionRepository.findByIsActiveTrueOrderByLastActivityDesc();
    }

    public List<ChatSession> getSessionsWithVisitor() {
        return chatSessionRepository.findByIsActiveTrueAndVisitorNameIsNotNull();
    }

    public ChatSession updateVisitorName(String sessionId, String name) {
        ChatSession session = getSessionById(sessionId);
        session.setVisitorName(name);
        session.setLastActivity(LocalDateTime.now());
        return chatSessionRepository.save(session);
    }

    public void closeSession(String sessionId) {
        ChatSession session = getSessionById(sessionId);
        session.setIsActive(false);
        chatSessionRepository.save(session);
    }

    public void updateActivity(String sessionId) {
        ChatSession session = getSessionById(sessionId);
        session.setLastActivity(LocalDateTime.now());
        chatSessionRepository.save(session);
    }
}