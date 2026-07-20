package com.africanmission.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DiagnosticController {

    @GetMapping("/diagnostiqueur")
    public String diagnostiqueur() {
        return "diagnostic";
    }
}