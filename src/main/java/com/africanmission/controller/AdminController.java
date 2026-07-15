package com.africanmission.controller;

import com.africanmission.model.Activity;
import com.africanmission.model.ContactMessage;
import com.africanmission.model.Partner;
import com.africanmission.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ActivityService activityService;
    private final PartnerService partnerService;
    private final ContactService contactService;
    private final NewsletterService newsletterService;
    private final ChatService chatService;
    private final ChatSessionService chatSessionService;

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activitiesCount", activityService.getAllActiveActivities().size());
        model.addAttribute("partnersCount", partnerService.getAllPartners().size());
        model.addAttribute("messagesCount", contactService.getAllMessages().size());
        model.addAttribute("subscribersCount", newsletterService.getSubscriberCount());
        model.addAttribute("activeChatSessions", chatSessionService.getActiveSessions().size());
        model.addAttribute("pageTitle", "Dashboard - Administration");
        return "admin/dashboard";
    }

    // ... (le reste du code est identique)

    // Gestion du chat - Redirection vers la nouvelle interface
    @GetMapping("/chat")
    public String manageChat() {
        return "redirect:/admin/chat-sessions";
    }

    @GetMapping("/chat-sessions")
    public String chatSessions(Model model) {
        model.addAttribute("pageTitle", "Chat en direct - Administration");
        return "admin/chat-sessions";
    }
}