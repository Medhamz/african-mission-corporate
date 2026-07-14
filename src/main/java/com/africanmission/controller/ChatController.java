package com.africanmission.controller;

import com.africanmission.model.ChatMessage;
import com.africanmission.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestBody ChatMessage message,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Récupérer l'IP
            message.setIpAddress(request.getRemoteAddr());
            message.setIsApproved(false);
            chatService.saveMessage(message);
            response.put("success", true);
            response.put("message", "Message envoyé avec succès");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessage>> getMessages() {
        return ResponseEntity.ok(chatService.getApprovedMessages());
    }
}