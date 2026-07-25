package com.africanmission.controller.admin;

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
    public String index(Model model) {
        model.addAttribute("slides", kioskService.getAllSlides());
        model.addAttribute("pageTitle", "Gestion du Mode Kiosque");
        return "admin/kiosk/slides";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("slide", new KioskSlide());
        model.addAttribute("pageTitle", "Ajouter une slide");
        return "admin/kiosk/slide-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("slide", kioskService.getSlideById(id));
        model.addAttribute("pageTitle", "Modifier une slide");
        return "admin/kiosk/slide-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute KioskSlide slide, RedirectAttributes redirectAttributes) {
        // On ne définit pas l'ID manuellement (il est généré automatiquement)
        // On conserve l'ordre existant si présent
        if (slide.getId() != null) {
            KioskSlide existing = kioskService.getSlideById(slide.getId());
            // On conserve l'ordre original (ou on le laisse)
            slide.setSlideOrder(existing.getSlideOrder());
            // On ne touche pas à la date de création
        }
        kioskService.saveSlide(slide);
        redirectAttributes.addFlashAttribute("toastMessage", "Slide enregistrée !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/kiosk";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        kioskService.deleteSlide(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Slide supprimée !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/kiosk";
    }

    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        kioskService.toggleActive(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Statut modifié !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/kiosk";
    }
}