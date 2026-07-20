package com.africanmission.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

    @GetMapping("/carte-monde")
    public String map() {
        return "map";
    }
}