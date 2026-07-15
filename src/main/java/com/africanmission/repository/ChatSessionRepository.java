package com.africanmission.repository;

import com.africanmission.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findBySessionId(String sessionId);
    List<ChatSession> findByIsActiveTrueOrderByLastActivityDesc();
    List<ChatSession> findByIsActiveTrueAndVisitorNameIsNotNull();
}