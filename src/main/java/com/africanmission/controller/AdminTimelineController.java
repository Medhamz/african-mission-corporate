package com.africanmission.controller.admin;

import com.africanmission.model.TimelineEvent;
import com.africanmission.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/timeline")
@RequiredArgsConstructor
public class AdminTimelineController {

    private final TimelineService timelineService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("events", timelineService.getAllEvents());
        model.addAttribute("pageTitle", "Gestion de la Timeline 3D");
        return "admin/timeline/events";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("event", new TimelineEvent());
        model.addAttribute("pageTitle", "Ajouter un événement");
        return "admin/timeline/event-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("event", timelineService.getEventById(id));
        model.addAttribute("pageTitle", "Modifier un événement");
        return "admin/timeline/event-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute TimelineEvent event, RedirectAttributes redirectAttributes) {
        timelineService.saveEvent(event);
        redirectAttributes.addFlashAttribute("toastMessage", "Événement enregistré !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/timeline";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        timelineService.deleteEvent(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Événement supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/timeline";
    }

    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        timelineService.toggleActive(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Statut modifié !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/timeline";
    }
}