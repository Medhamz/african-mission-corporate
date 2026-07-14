package com.africanmission.controller;

import com.africanmission.service.NewsletterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/newsletter")
@RequiredArgsConstructor
public class NewsletterController {

    private final NewsletterService newsletterService;

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam("email") String email,
                            RedirectAttributes redirectAttributes) {
        try {
            newsletterService.subscribe(email);
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Merci ! Vous êtes désormais abonné à notre newsletter.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ " + e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/unsubscribe")
    public String unsubscribe(@RequestParam("email") String email,
                              RedirectAttributes redirectAttributes) {
        try {
            newsletterService.unsubscribe(email);
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Vous avez été désabonné avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ " + e.getMessage());
        }
        return "redirect:/";
    }
}