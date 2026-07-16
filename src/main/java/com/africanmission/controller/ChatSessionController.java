package com.africanmission.controller;

import com.africanmission.model.ChatSession;
import com.africanmission.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat/session")
@RequiredArgsConstructor
public class ChatSessionController {

    private static final Logger logger = LoggerFactory.getLogger(ChatSessionController.class);
    private final ChatSessionService chatSessionService;

    @PostMapping("/init")
    public ResponseEntity<Map<String, Object>> initSession(HttpServletRequest request, HttpSession httpSession) {
        Map<String, Object> response = new HashMap<>();
        String ipAddress = request.getRemoteAddr();

        String chatSessionId = (String) httpSession.getAttribute("chatSessionId");
        ChatSession session;

        if (chatSessionId == null) {
            session = chatSessionService.createSession(ipAddress);
            httpSession.setAttribute("chatSessionId", session.getSessionId());
        } else {
            session = chatSessionService.getOrCreateSession(chatSessionId, ipAddress);
        }

        response.put("sessionId", session.getSessionId());
        response.put("visitorName", session.getVisitorName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/set-name")
    public ResponseEntity<Map<String, Object>> setVisitorName(
            @RequestParam String sessionId,
            @RequestParam String name) {
        Map<String, Object> response = new HashMap<>();
        ChatSession session = chatSessionService.updateVisitorName(sessionId, name);
        if (session != null) {
            response.put("success", true);
            response.put("visitorName", session.getVisitorName());
        } else {
            response.put("success", false);
            response.put("message", "Session non trouvée");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Map<String, Object>>> getActiveSessions() {
        List<ChatSession> sessions = chatSessionService.getActiveSessions();
        List<Map<String, Object>> sessionList = new ArrayList<>();

        for (ChatSession session : sessions) {
            Map<String, Object> sessionMap = new HashMap<>();
            sessionMap.put("id", session.getId());
            sessionMap.put("sessionId", session.getSessionId());
            sessionMap.put("visitorName", session.getVisitorName() != null ? session.getVisitorName() : "Visiteur");
            sessionMap.put("isActive", session.getIsActive());
            sessionMap.put("lastActivity", session.getLastActivity() != null ? session.getLastActivity().toString() : null);
            sessionMap.put("startedAt", session.getStartedAt() != null ? session.getStartedAt().toString() : null);
            sessionList.add(sessionMap);
        }

        return ResponseEntity.ok(sessionList);
    }

    @GetMapping("/with-visitor")
    public ResponseEntity<List<Map<String, Object>>> getSessionsWithVisitor() {
        List<ChatSession> sessions = chatSessionService.getSessionsWithVisitor();
        List<Map<String, Object>> sessionList = new ArrayList<>();

        for (ChatSession session : sessions) {
            Map<String, Object> sessionMap = new HashMap<>();
            sessionMap.put("id", session.getId());
            sessionMap.put("sessionId", session.getSessionId());
            sessionMap.put("visitorName", session.getVisitorName() != null ? session.getVisitorName() : "Visiteur");
            sessionMap.put("isActive", session.getIsActive());
            sessionMap.put("lastActivity", session.getLastActivity() != null ? session.getLastActivity().toString() : null);
            sessionMap.put("startedAt", session.getStartedAt() != null ? session.getStartedAt().toString() : null);
            sessionList.add(sessionMap);
        }

        logger.info("📋 {} sessions avec visiteur retournées", sessionList.size());
        return ResponseEntity.ok(sessionList);
    }

    @PostMapping("/close")
    public ResponseEntity<Map<String, Object>> closeSession(@RequestParam String sessionId) {
        Map<String, Object> response = new HashMap<>();
        chatSessionService.closeSession(sessionId);
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}