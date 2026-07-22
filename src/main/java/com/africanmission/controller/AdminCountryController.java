package com.africanmission.controller;

import com.africanmission.model.Country;
import com.africanmission.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/countries")
@RequiredArgsConstructor
public class AdminCountryController {

    private final CountryService countryService;

    @GetMapping
    public String listCountries(Model model) {
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("pageTitle", "Gestion des pays");
        return "admin/countries";
    }

    @GetMapping("/new")
    public String newCountryForm(Model model) {
        model.addAttribute("country", new Country());
        model.addAttribute("pageTitle", "Ajouter un pays");
        return "admin/country-form";
    }

    @GetMapping("/edit/{id}")
    public String editCountryForm(@PathVariable Long id, Model model) {
        Country country = countryService.getCountryById(id)
                .orElseThrow(() -> new RuntimeException("Pays non trouvé"));
        model.addAttribute("country", country);
        model.addAttribute("pageTitle", "Modifier un pays");
        return "admin/country-form";
    }

    @PostMapping("/save")
    public String saveCountry(@ModelAttribute Country country, RedirectAttributes redirectAttributes) {
        try {
            countryService.saveCountry(country);
            redirectAttributes.addFlashAttribute("toastMessage", "Pays enregistré avec succès !");
            redirectAttributes.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", "Erreur : " + e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/countries";
    }

    @PostMapping("/delete/{id}")
    public String deleteCountry(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            countryService.deleteCountry(id);
            redirectAttributes.addFlashAttribute("toastMessage", "Pays supprimé !");
            redirectAttributes.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", "Erreur : " + e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/countries";
    }

    @PostMapping("/toggle/{id}")
    public String toggleCountry(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            countryService.toggleActive(id);
            redirectAttributes.addFlashAttribute("toastMessage", "Statut du pays modifié !");
            redirectAttributes.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", "Erreur : " + e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/countries";
    }
}