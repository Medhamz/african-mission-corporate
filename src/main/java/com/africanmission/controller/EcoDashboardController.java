package com.africanmission.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Controller
public class EcoDashboardController {

    @GetMapping("/eco")
    public String ecoDashboard() {
        return "eco-dashboard";
    }
}

@RestController
@RequestMapping("/api/eco")
class EcoDataController {

    @GetMapping("/indicators")
    public Map<String, Object> getIndicators() {
        Map<String, Object> data = new LinkedHashMap<>();

        // 1. Empreinte carbone
        Map<String, Object> carbon = new LinkedHashMap<>();
        carbon.put("total", 245.6); // tonnes CO2
        carbon.put("target", 200.0);
        carbon.put("trend", -5.2); // % d'évolution
        carbon.put("breakdown", Map.of(
                "BTP", 120.4,
                "Agrobusiness", 68.2,
                "Import-Export", 57.0
        ));
        data.put("carbon", carbon);

        // 2. Consommation d'eau (m³)
        Map<String, Object> water = new LinkedHashMap<>();
        water.put("total", 18450);
        water.put("target", 15000);
        water.put("trend", -8.1);
        water.put("breakdown", Map.of(
                "BTP", 8200,
                "Agrobusiness", 7250,
                "Import-Export", 3000
        ));
        data.put("water", water);

        // 3. Consommation d'énergie (MWh)
        Map<String, Object> energy = new LinkedHashMap<>();
        energy.put("total", 342.8);
        energy.put("target", 300.0);
        energy.put("trend", -3.5);
        energy.put("breakdown", Map.of(
                "BTP", 180.2,
                "Agrobusiness", 102.6,
                "Import-Export", 60.0
        ));
        data.put("energy", energy);

        // 4. Taux de recyclage (%)
        Map<String, Object> recycling = new LinkedHashMap<>();
        recycling.put("total", 76.4);
        recycling.put("target", 85.0);
        recycling.put("trend", 6.8);
        data.put("recycling", recycling);

        // 5. Impact social
        Map<String, Object> social = new LinkedHashMap<>();
        social.put("localJobs", 124);
        social.put("trainings", 85);
        social.put("volunteerHours", 320);
        social.put("womenInLeadership", 38.5);
        data.put("social", social);

        // 6. Objectifs RSE (progression en %)
        Map<String, Object> goals = new LinkedHashMap<>();
        goals.put("neutraliteCarbone", 62);
        goals.put("eauResponsable", 78);
        goals.put("dechetsZero", 45);
        goals.put("energieVerte", 83);
        data.put("goals", goals);

        // 7. Certifications
        data.put("certifications", Arrays.asList(
                "ISO 14001 (2025)",
                "Label Éco-Entreprise",
                "Engagement RSE de l'UE"
        ));

        return data;
    }

    @GetMapping("/history")
    public List<Map<String, Object>> getHistory() {
        // Historique des 12 derniers mois (simulé)
        List<Map<String, Object>> history = new ArrayList<>();
        String[] months = {"Août 2025", "Sep 2025", "Oct 2025", "Nov 2025", "Déc 2025", "Jan 2026", "Fév 2026", "Mar 2026", "Avr 2026", "Mai 2026", "Juin 2026", "Juil 2026"};
        double[] carbonValues = {280, 275, 260, 250, 240, 235, 225, 220, 215, 210, 205, 200};
        double[] waterValues = {20000, 19500, 19000, 18500, 18000, 17500, 17000, 16500, 16000, 15500, 15000, 14500};

        for (int i = 0; i < months.length; i++) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("month", months[i]);
            entry.put("carbon", carbonValues[i] + Math.random() * 10 - 5);
            entry.put("water", waterValues[i] + Math.random() * 200 - 100);
            history.add(entry);
        }
        return history;
    }

    @GetMapping("/tips")
    public List<Map<String, String>> getTips() {
        return Arrays.asList(
                Map.of("title", "Réduisez vos déplacements", "desc", "Utilisez les visioconférences pour limiter l'empreinte carbone."),
                Map.of("title", "Optimisez l'irrigation", "desc", "Adoptez des systèmes d'irrigation goutte-à-goutte pour économiser l'eau."),
                Map.of("title", "Énergie solaire", "desc", "Installez des panneaux solaires sur vos chantiers pour réduire l'empreinte énergétique."),
                Map.of("title", "Recyclage des déchets", "desc", "Mettez en place un système de tri et de recyclage sur tous vos sites."),
                Map.of("title", "Sensibilisation des équipes", "desc", "Organisez des ateliers RSE pour impliquer vos collaborateurs.")
        );
    }
}