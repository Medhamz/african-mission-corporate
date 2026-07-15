package com.africanmission.controller;

import com.africanmission.model.ChatSession;
import com.africanmission.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat/session")
@RequiredArgsConstructor
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    @PostMapping("/init")
    public ResponseEntity<Map<String, Object>> initSession(HttpServletRequest request, HttpSession httpSession) {
        Map<String, Object> response = new HashMap<>();
        String ipAddress = request.getRemoteAddr();

        // Utiliser la session HTTP pour stocker l'ID de session chat
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
        response.put("success", true);
        response.put("visitorName", session.getVisitorName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ChatSession>> getActiveSessions() {
        return ResponseEntity.ok(chatSessionService.getActiveSessions());
    }

    @GetMapping("/with-visitor")
    public ResponseEntity<List<ChatSession>> getSessionsWithVisitor() {
        return ResponseEntity.ok(chatSessionService.getSessionsWithVisitor());
    }

    @PostMapping("/close")
    public ResponseEntity<Map<String, Object>> closeSession(@RequestParam String sessionId) {
        Map<String, Object> response = new HashMap<>();
        chatSessionService.closeSession(sessionId);
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}