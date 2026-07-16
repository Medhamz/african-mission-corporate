package com.africanmission.controller;

import com.africanmission.model.ChatMessage;
import com.africanmission.model.ChatSession;
import com.africanmission.service.ChatService;
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
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
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
            String username = payload.get("username");

            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = (String) httpSession.getAttribute("chatSessionId");
            }

            if (sessionId == null || sessionId.isEmpty()) {
                response.put("success", false);
                response.put("message", "Session non initialisée");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("📩 Message reçu - Session: {}, Sender: {}", sessionId, sender);

            ChatSession session = chatSessionService.getSessionById(sessionId);
            if (session == null) {
                response.put("success", false);
                response.put("message", "Session non trouvée");
                return ResponseEntity.badRequest().body(response);
            }

            if (username != null && !username.isEmpty()) {
                session.setVisitorName(username);
                chatSessionService.updateVisitorName(sessionId, username);
            }

            chatSessionService.updateActivity(sessionId);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSession(session);
            chatMessage.setMessage(message);
            chatMessage.setSender(sender);
            chatMessage.setIsFromAdmin("admin".equals(sender));
            chatMessage.setIsRead(false);
            chatMessage.setUsername(username);
            chatMessage.setIpAddress(request.getRemoteAddr());

            chatService.saveMessage(chatMessage);

            response.put("success", true);
            response.put("message", "Message envoyé");
        } catch (Exception e) {
            logger.error("❌ Erreur envoi message: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessages(
            @RequestParam String sessionId) {

        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("📋 Récupération des messages pour la session: {}", sessionId);

            ChatSession session = chatSessionService.getSessionById(sessionId);
            if (session == null) {
                response.put("sessionId", sessionId);
                response.put("messages", new ArrayList<>());
                response.put("visitorName", null);
                return ResponseEntity.ok(response);
            }

            List<ChatMessage> messages = chatService.getMessagesBySession(session.getId());
            chatService.markAllAsRead(session.getId());

            // ✅ Construction correcte du tableau
            List<Map<String, Object>> messageList = new ArrayList<>();
            if (messages != null) {
                for (ChatMessage msg : messages) {
                    Map<String, Object> msgMap = new HashMap<>();
                    msgMap.put("id", msg.getId());
                    msgMap.put("sender", msg.getSender() != null ? msg.getSender() : "visitor");
                    msgMap.put("message", msg.getMessage());
                    msgMap.put("isRead", msg.getIsRead());
                    msgMap.put("isFromAdmin", msg.getIsFromAdmin());
                    msgMap.put("username", msg.getUsername());
                    msgMap.put("sentAt", msg.getSentAt() != null ? msg.getSentAt().toString() : null);
                    msgMap.put("createdAt", msg.getCreatedAt() != null ? msg.getCreatedAt().toString() : null);
                    messageList.add(msgMap);
                }
            }

            response.put("sessionId", sessionId);
            response.put("messages", messageList);
            response.put("visitorName", session.getVisitorName() != null ? session.getVisitorName() : "Visiteur");

            logger.info("✅ {} messages récupérés", messageList.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ Erreur récupération messages: {}", e.getMessage(), e);
            response.put("sessionId", sessionId);
            response.put("messages", new ArrayList<>());
            response.put("visitorName", null);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/session-info")
    public ResponseEntity<Map<String, Object>> getSessionInfo(HttpSession httpSession) {
        Map<String, Object> response = new HashMap<>();
        String sessionId = (String) httpSession.getAttribute("chatSessionId");
        if (sessionId != null) {
            try {
                ChatSession session = chatSessionService.getSessionById(sessionId);
                response.put("sessionId", session.getSessionId());
                response.put("visitorName", session.getVisitorName());
                response.put("isActive", session.getIsActive());
            } catch (Exception e) {
                response.put("sessionId", null);
                response.put("visitorName", null);
                response.put("isActive", false);
            }
        } else {
            response.put("sessionId", null);
            response.put("visitorName", null);
        }
        return ResponseEntity.ok(response);
    }
}