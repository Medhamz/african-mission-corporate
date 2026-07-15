package com.africanmission.service;

import com.africanmission.model.ChatSession;
import com.africanmission.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private static final Logger logger = LoggerFactory.getLogger(ChatSessionService.class);
    private final ChatSessionRepository chatSessionRepository;

    public ChatSession createSession(String ipAddress) {
        ChatSession session = new ChatSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setIpAddress(ipAddress);
        session.setIsActive(true);
        session.setLastActivity(LocalDateTime.now());
        logger.info("✅ Nouvelle session créée: {}", session.getSessionId());
        return chatSessionRepository.save(session);
    }

    public ChatSession getOrCreateSession(String sessionId, String ipAddress) {
        if (sessionId != null && !sessionId.isEmpty()) {
            return chatSessionRepository.findBySessionId(sessionId)
                    .orElseGet(() -> createSession(ipAddress));
        }
        return createSession(ipAddress);
    }

    public ChatSession getSessionById(String sessionId) {
        return chatSessionRepository.findBySessionId(sessionId)
                .orElse(null);
    }

    public List<ChatSession> getActiveSessions() {
        return chatSessionRepository.findByIsActiveTrueOrderByLastActivityDesc();
    }

    public List<ChatSession> getSessionsWithVisitor() {
        return chatSessionRepository.findByIsActiveTrueAndVisitorNameIsNotNull();
    }

    public ChatSession updateVisitorName(String sessionId, String name) {
        ChatSession session = getSessionById(sessionId);
        if (session != null) {
            session.setVisitorName(name);
            session.setLastActivity(LocalDateTime.now());
            logger.info("✅ Nom du visiteur mis à jour: {} pour la session {}", name, sessionId);
            return chatSessionRepository.save(session);
        }
        logger.warn("⚠️ Session non trouvée pour mise à jour du nom: {}", sessionId);
        return null;
    }

    public void closeSession(String sessionId) {
        ChatSession session = getSessionById(sessionId);
        if (session != null) {
            session.setIsActive(false);
            chatSessionRepository.save(session);
            logger.info("✅ Session fermée: {}", sessionId);
        }
    }

    public void updateActivity(String sessionId) {
        ChatSession session = getSessionById(sessionId);
        if (session != null) {
            session.setLastActivity(LocalDateTime.now());
            chatSessionRepository.save(session);
        }
    }
}