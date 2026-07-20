package com.africanmission.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WorldMapController {

    @GetMapping("/monde")
    public String worldMap() {
        return "world-map";
    }
}

// Ne pas oublier ce RestController pour les données API
@RestController
@RequestMapping("/api/world")
class WorldDataController {

    @GetMapping("/countries")
    public List<Map<String, Object>> getCountries() {
        return Arrays.asList(
                createCountry("Mali", 12.65, -8.0, "Bamako", 3, 2, "💰 1.2M FCFA"),
                createCountry("Sénégal", 14.5, -14.5, "Dakar", 2, 1, "💰 850K FCFA"),
                createCountry("Côte d'Ivoire", 6.8, -5.3, "Abidjan", 1, 0, "💰 620K FCFA"),
                createCountry("France", 48.9, 2.3, "Paris", 1, 1, "💰 2.1M FCFA"),
                createCountry("États-Unis", 40.7, -74.0, "New York", 0, 1, "💰 3.5M FCFA")
        );
    }

    private Map<String, Object> createCountry(String name, double lat, double lng, String capital, int projects, int partners, String investment) {
        Map<String, Object> country = new LinkedHashMap<>();
        country.put("name", name);
        country.put("lat", lat);
        country.put("lng", lng);
        country.put("capital", capital);
        country.put("projects", projects);
        country.put("partners", partners);
        country.put("investment", investment);
        country.put("flag", getFlagEmoji(name));
        return country;
    }

    private String getFlagEmoji(String country) {
        Map<String, String> flags = new LinkedHashMap<>();
        flags.put("Mali", "🇲🇱");
        flags.put("Sénégal", "🇸🇳");
        flags.put("Côte d'Ivoire", "🇨🇮");
        flags.put("France", "🇫🇷");
        flags.put("États-Unis", "🇺🇸");
        return flags.getOrDefault(country, "🌍");
    }
}