package com.africanmission.repository;

import com.africanmission.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySessionIdOrderBySentAtAsc(Long sessionId);
    List<ChatMessage> findBySessionIdAndIsReadFalse(Long sessionId);
    List<ChatMessage> findTop10BySessionIdOrderBySentAtDesc(Long sessionId);
}