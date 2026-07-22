package com.africanmission.controller.admin;

import com.africanmission.model.EcoGoal;
import com.africanmission.model.EcoIndicator;
import com.africanmission.model.EcoTip;
import com.africanmission.service.EcoDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/eco")
@RequiredArgsConstructor
public class AdminEcoController {

    private final EcoDashboardService ecoService;

    // === INDICATORS ===
    @GetMapping("/indicators")
    public String indicators(Model model) {
        model.addAttribute("indicators", ecoService.getAllIndicators());
        model.addAttribute("pageTitle", "Gestion des indicateurs éco");
        return "admin/eco/indicators";
    }

    @GetMapping("/indicators/add")
    public String addIndicatorForm(Model model) {
        model.addAttribute("indicator", new EcoIndicator());
        model.addAttribute("pageTitle", "Ajouter un indicateur");
        return "admin/eco/indicator-form";
    }

    @PostMapping("/indicators/save")
    public String saveIndicator(@ModelAttribute EcoIndicator indicator, RedirectAttributes redirectAttributes) {
        ecoService.saveIndicator(indicator);
        redirectAttributes.addFlashAttribute("toastMessage", "Indicateur enregistré !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/eco/indicators";
    }

    @GetMapping("/indicators/delete/{id}")
    public String deleteIndicator(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ecoService.deleteIndicator(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Indicateur supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/eco/indicators";
    }

    // === GOALS ===
    @GetMapping("/goals")
    public String goals(Model model) {
        model.addAttribute("goals", ecoService.getAllGoals());
        model.addAttribute("pageTitle", "Gestion des objectifs RSE");
        return "admin/eco/goals";
    }

    @GetMapping("/goals/add")
    public String addGoalForm(Model model) {
        model.addAttribute("goal", new EcoGoal());
        model.addAttribute("pageTitle", "Ajouter un objectif RSE");
        return "admin/eco/goal-form";
    }

    @PostMapping("/goals/save")
    public String saveGoal(@ModelAttribute EcoGoal goal, RedirectAttributes redirectAttributes) {
        ecoService.saveGoal(goal);
        redirectAttributes.addFlashAttribute("toastMessage", "Objectif enregistré !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/eco/goals";
    }

    @GetMapping("/goals/delete/{id}")
    public String deleteGoal(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ecoService.deleteGoal(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Objectif supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/eco/goals";
    }

    // === TIPS ===
    @GetMapping("/tips")
    public String tips(Model model) {
        model.addAttribute("tips", ecoService.getAllTips());
        model.addAttribute("pageTitle", "Gestion des conseils éco");
        return "admin/eco/tips";
    }

    @GetMapping("/tips/add")
    public String addTipForm(Model model) {
        model.addAttribute("tip", new EcoTip());
        model.addAttribute("pageTitle", "Ajouter un conseil");
        return "admin/eco/tip-form";
    }

    @PostMapping("/tips/save")
    public String saveTip(@ModelAttribute EcoTip tip, RedirectAttributes redirectAttributes) {
        ecoService.saveTip(tip);
        redirectAttributes.addFlashAttribute("toastMessage", "Conseil enregistré !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/eco/tips";
    }

    @GetMapping("/tips/delete/{id}")
    public String deleteTip(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ecoService.deleteTip(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Conseil supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/eco/tips";
    }
}