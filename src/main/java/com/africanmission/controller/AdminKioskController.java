package com.africanmission.controller;

import com.africanmission.model.KioskSlide;
import com.africanmission.service.KioskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin/kiosk")
@RequiredArgsConstructor
public class AdminKioskController {

    private final KioskService kioskService;

    @GetMapping
    public String manageKiosk(Model model) {
        model.addAttribute("slides", kioskService.getAllSlides());
        model.addAttribute("pageTitle", "Gestion du Mode Kiosque");
        return "admin/kiosk";
    }

    @GetMapping("/add")
    public String addSlideForm(Model model) {
        model.addAttribute("slide", new KioskSlide());
        model.addAttribute("pageTitle", "Ajouter une slide");
        return "admin/kiosk-form";
    }

    @PostMapping("/add")
    public String addSlide(@ModelAttribute KioskSlide slide, RedirectAttributes redirectAttributes) {
        try {
            // Définir l'ordre automatiquement
            List<KioskSlide> existingSlides = kioskService.getAllSlides();
            slide.setSlideOrder(existingSlides.size() + 1);
            kioskService.saveSlide(slide);
            redirectAttributes.addFlashAttribute("toastMessage", "Slide ajoutée avec succès !");
            redirectAttributes.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", "Erreur : " + e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/kiosk";
    }

    @GetMapping("/edit/{id}")
    public String editSlideForm(@PathVariable Long id, Model model) {
        model.addAttribute("slide", kioskService.getSlideById(id));
        model.addAttribute("pageTitle", "Modifier la slide");
        return "admin/kiosk-form";
    }

    @PostMapping("/edit/{id}")
    public String editSlide(@PathVariable Long id, @ModelAttribute KioskSlide slide, RedirectAttributes redirectAttributes) {
        try {
            KioskSlide existingSlide = kioskService.getSlideById(id);
            slide.setId(id);
            slide.setSlideOrder(existingSlide.getSlideOrder());
            slide.setCreatedAt(existingSlide.getCreatedAt());
            kioskService.saveSlide(slide);
            redirectAttributes.addFlashAttribute("toastMessage", "Slide modifiée avec succès !");
            redirectAttributes.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", "Erreur : " + e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/kiosk";
    }

    @GetMapping("/delete/{id}")
    public String deleteSlide(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            kioskService.deleteSlide(id);
            redirectAttributes.addFlashAttribute("toastMessage", "Slide supprimée !");
            redirectAttributes.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", "Erreur : " + e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/kiosk";
    }

    @PostMapping("/toggle/{id}")
    public String toggleSlide(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            kioskService.toggleActive(id);
            redirectAttributes.addFlashAttribute("toastMessage", "Statut modifié !");
            redirectAttributes.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", "Erreur : " + e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/kiosk";
    }

    @PostMapping("/reorder")
    @ResponseBody
    public String reorderSlides(@RequestBody List<Long> slideIds) {
        kioskService.reorderSlides(slideIds);
        return "OK";
    }
}