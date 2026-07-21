package com.africanmission.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KioskController {

    @GetMapping("/kiosk")
    public String kiosk() {
        return "kiosk";
    }
}