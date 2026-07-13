package com.africanmission.controller;

import com.africanmission.model.ContactMessage;
import com.africanmission.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    public String showContactForm(ContactMessage contactMessage) {
        return "contact";
    }

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
}