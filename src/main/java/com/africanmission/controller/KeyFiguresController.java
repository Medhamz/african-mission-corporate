package com.africanmission.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KeyFiguresController {

    @GetMapping("/key-figures")
    public String keyFigures(Model model) {
        model.addAttribute("pageTitle", "Chiffres Clés - African Mission Corporate");
        return "key-figures"; // Fait référence à key-figures.html dans templates/
    }
}