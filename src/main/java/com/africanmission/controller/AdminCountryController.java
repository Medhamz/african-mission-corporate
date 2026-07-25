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
    public String index(Model model) {
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("pageTitle", "Gestion de la carte du monde");
        return "admin/countries/countries";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("country", new Country());
        model.addAttribute("pageTitle", "Ajouter un pays");
        return "admin/countries/country-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        // CORRECTION : pas de orElseThrow ici
        model.addAttribute("country", countryService.getCountryById(id));
        model.addAttribute("pageTitle", "Modifier un pays");
        return "admin/countries/country-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Country country, RedirectAttributes redirectAttributes) {
        countryService.saveCountry(country);
        redirectAttributes.addFlashAttribute("toastMessage", "Pays enregistré !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/countries";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        countryService.deleteCountry(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Pays supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/countries";
    }

    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        countryService.toggleActive(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Statut modifié !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/countries";
    }
}