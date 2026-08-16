package com.africanmission.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DiagnosticController {

    @GetMapping({"/diagnostiqueur", "/diagnostic", "/diagnostic.html"})
    public String diagnostiqueur() {
        return "diagnostic"; // Renvoie le template templates/diagnostic.html
    }
}