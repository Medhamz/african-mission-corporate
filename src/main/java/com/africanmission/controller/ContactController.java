package com.africanmission.controller;

import com.africanmission.model.ContactMessage;
import com.africanmission.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    // ✅ AFFICHER LE FORMULAIRE DE CONTACT (GET)
    @GetMapping
    public String showContactForm(Model model) {
        model.addAttribute("contactMessage", new ContactMessage());
        model.addAttribute("pageTitle", "Contact - African Mission Corporate");
        return "contact";
    }

    // ✅ TRAITER LE FORMULAIRE DE CONTACT (POST - Version classique)
    @PostMapping
    public String handleContactForm(@Valid ContactMessage contactMessage,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "contact";
        }

        contactService.saveMessage(contactMessage);
        redirectAttributes.addFlashAttribute("successMessage",
                "Votre message a été envoyé avec succès ! Nous vous répondrons dans les plus brefs délais.");

        return "redirect:/contact";
    }

    // ✅ TRAITER LE FORMULAIRE DE CONTACT (POST - Version AJAX)
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