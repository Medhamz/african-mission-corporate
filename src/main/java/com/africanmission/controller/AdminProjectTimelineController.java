package com.africanmission.controller.admin;

import com.africanmission.model.ProjectTimeline;
import com.africanmission.service.ProjectTimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/projects-timeline")
@RequiredArgsConstructor
public class AdminProjectTimelineController {

    private final ProjectTimelineService projectTimelineService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("projects", projectTimelineService.getAllProjects());
        model.addAttribute("pageTitle", "Gestion du projet timeline");
        return "admin/projects/timeline";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("project", new ProjectTimeline());
        model.addAttribute("pageTitle", "Ajouter un projet");
        return "admin/projects/project-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("project", projectTimelineService.getProjectById(id));
        model.addAttribute("pageTitle", "Modifier un projet");
        return "admin/projects/project-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ProjectTimeline project, RedirectAttributes redirectAttributes) {
        projectTimelineService.saveProject(project);
        redirectAttributes.addFlashAttribute("toastMessage", "Projet enregistré !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/projects-timeline";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        projectTimelineService.deleteProject(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Projet supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/projects-timeline";
    }

    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        projectTimelineService.toggleActive(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Statut modifié !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/projects-timeline";
    }
}