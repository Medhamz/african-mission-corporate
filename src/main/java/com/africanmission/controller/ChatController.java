package com.africanmission.controller;

import com.africanmission.model.ChatMessage;
import com.africanmission.model.ChatSession;
import com.africanmission.service.ChatService;
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
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatSessionService chatSessionService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestBody Map<String, String> payload,
            HttpServletRequest request,
            HttpSession httpSession) {

        Map<String, Object> response = new HashMap<>();
        try {
            String sessionId = payload.get("sessionId");
            String message = payload.get("message");
            String sender = payload.get("sender") != null ? payload.get("sender") : "visitor";

            if (sessionId == null) {
                // Si pas de sessionId, on utilise la session HTTP
                sessionId = (String) httpSession.getAttribute("chatSessionId");
            }

            if (sessionId == null) {
                response.put("success", false);
                response.put("message", "Session non initialisée");
                return ResponseEntity.badRequest().body(response);
            }

            ChatSession session = chatSessionService.getSessionById(sessionId);
            chatSessionService.updateActivity(sessionId);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSession(session);
            chatMessage.setMessage(message);
            chatMessage.setSender(sender);
            chatMessage.setIsFromAdmin("admin".equals(sender));
            chatMessage.setIsRead(false);

            chatService.saveMessage(chatMessage);

            response.put("success", true);
            response.put("message", "Message envoyé");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessages(
            @RequestParam String sessionId,
            @RequestParam(defaultValue = "50") int limit) {

        Map<String, Object> response = new HashMap<>();
        ChatSession session = chatSessionService.getSessionById(sessionId);
        List<ChatMessage> messages = chatService.getMessagesBySession(session.getId());

        // Marquer les messages comme lus
        chatService.markAllAsRead(session.getId());

        response.put("sessionId", sessionId);
        response.put("messages", messages);
        response.put("visitorName", session.getVisitorName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session-info")
    public ResponseEntity<Map<String, Object>> getSessionInfo(HttpSession httpSession) {
        Map<String, Object> response = new HashMap<>();
        String sessionId = (String) httpSession.getAttribute("chatSessionId");
        if (sessionId != null) {
            ChatSession session = chatSessionService.getSessionById(sessionId);
            response.put("sessionId", session.getSessionId());
            response.put("visitorName", session.getVisitorName());
            response.put("isActive", session.getIsActive());
        } else {
            response.put("sessionId", null);
            response.put("visitorName", null);
        }
        return ResponseEntity.ok(response);
    }
}