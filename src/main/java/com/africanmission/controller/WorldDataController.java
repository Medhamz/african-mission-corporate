package com.africanmission.controller;

import com.africanmission.model.Country;
import com.africanmission.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/world")
@RequiredArgsConstructor
public class WorldDataController {

    private final CountryService countryService;

    @GetMapping("/countries")
    public List<Map<String, Object>> getCountries() {
        List<Country> activeCountries = countryService.getActiveCountries();
        return activeCountries.stream().map(country -> Map.of(
                "name", country.getName(),
                "lat", country.getLat(),
                "lng", country.getLng(),
                "capital", country.getCapital(),
                "projects", country.getProjectsCount(),
                "partners", country.getPartnersCount(),
                "investment", country.getInvestment(),
                "flag", country.getFlag(),
                "type", country.getType()
        )).collect(Collectors.toList());
    }
}