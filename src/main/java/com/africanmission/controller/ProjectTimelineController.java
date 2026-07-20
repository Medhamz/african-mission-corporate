package com.africanmission.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectTimelineController {

    @GetMapping("/timeline")
    public List<Map<String, Object>> getTimeline() {
        // Données simulées de projets (nom, date début, date fin estimée, lieu pour météo)
        return Arrays.asList(
                Map.of(
                        "id", 1,
                        "name", "Construction siège social",
                        "start", LocalDate.of(2026, 7, 1),
                        "end", LocalDate.of(2026, 9, 15),
                        "location", "Bamako",
                        "status", "En cours"
                ),
                Map.of(
                        "id", 2,
                        "name", "Projet agricole Sikasso",
                        "start", LocalDate.of(2026, 8, 10),
                        "end", LocalDate.of(2026, 11, 30),
                        "location", "Sikasso",
                        "status", "À venir"
                ),
                Map.of(
                        "id", 3,
                        "name", "Import-Export – Coton",
                        "start", LocalDate.of(2026, 9, 1),
                        "end", LocalDate.of(2026, 10, 20),
                        "location", "Dakar",
                        "status", "En cours"
                ),
                Map.of(
                        "id", 4,
                        "name", "Rénovation agrobusiness",
                        "start", LocalDate.of(2026, 10, 1),
                        "end", LocalDate.of(2026, 12, 15),
                        "location", "Bamako",
                        "status", "Prévu"
                )
        );
    }
}