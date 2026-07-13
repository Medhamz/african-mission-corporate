package com.africanmission.controller;

import com.africanmission.model.Activity;
import com.africanmission.model.Partner;
import com.africanmission.service.ActivityService;
import com.africanmission.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ActivityService activityService;
    private final PartnerService partnerService;

    @GetMapping("/")
    public String home(Model model) {
        List<Activity> activities = activityService.getAllActiveActivities();
        List<Partner> partners = partnerService.getAllActivePartners();

        model.addAttribute("activities", activities);
        model.addAttribute("partners", partners);
        model.addAttribute("pageTitle", "African Mission Corporate - Excellence et Innovation");

        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "À propos - African Mission Corporate");
        return "about";
    }

    @GetMapping("/activities")
    public String activities(Model model) {
        List<Activity> activities = activityService.getAllActiveActivities();
        List<String> categories = activityService.getAllCategories();

        model.addAttribute("activities", activities);
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Nos activités - African Mission Corporate");

        return "activities";
    }
}