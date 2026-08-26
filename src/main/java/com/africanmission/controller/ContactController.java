package com.africanmission.controller;

import com.africanmission.model.ContactMessage;
import com.africanmission.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    public String showContactForm(Model model) {
        if (!model.containsAttribute("contactMessage")) {
            model.addAttribute("contactMessage", new ContactMessage());
        }
        model.addAttribute("pageTitle", "Contact - African Mission Corporate");
        return "contact";
    }

    // ✅ Soumission standard HTML (Formulaire)
    @PostMapping
    public String handleContactFormSubmit(@ModelAttribute ContactMessage contactMessage,
                                          RedirectAttributes redirectAttributes) {
        try {
            contactService.saveMessage(contactMessage);
            redirectAttributes.addFlashAttribute("successMessage", "Votre message a été envoyé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors de l'envoi : " + e.getMessage());
        }
        return "redirect:/contact";
    }

    // ✅ Soumission AJAX
    @PostMapping("/ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleContactFormAjax(@RequestBody ContactMessage contactMessage) {
        Map<String, Object> response = new HashMap<>();
        try {
            contactService.saveMessage(contactMessage);
            response.put("success", true);
            response.put("message", "Votre message a été envoyé avec succès !");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}