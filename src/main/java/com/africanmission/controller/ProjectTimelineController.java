package com.africanmission.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectTimelineController {

    @GetMapping("/timeline")
    public List<Map<String, Object>> getTimeline() {
        return Arrays.asList(
                createProject(1, "Construction siège social", "2026-07-01", "2026-09-15", "Bamako", "En cours", 2),
                createProject(2, "Projet agricole Sikasso", "2026-08-10", "2026-11-30", "Sikasso", "À venir", 0),
                createProject(3, "Import-Export – Coton", "2026-09-01", "2026-10-20", "Dakar", "En cours", 1),
                createProject(4, "Rénovation agrobusiness", "2026-10-01", "2026-12-15", "Bamako", "Prévu", 3)
        );
    }

    private Map<String, Object> createProject(int id, String name, String start, String end, String location, String status, int rainDays) {
        Map<String, Object> project = new LinkedHashMap<>();
        project.put("id", id);
        project.put("name", name);
        project.put("start", start);
        project.put("end", end);
        project.put("location", location);
        project.put("status", status);
        project.put("rainDays", rainDays);
        return project;
    }
}