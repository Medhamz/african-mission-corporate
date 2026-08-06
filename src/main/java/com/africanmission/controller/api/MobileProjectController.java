package com.africanmission.controller.api;

import com.africanmission.model.Project;
import com.africanmission.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mobile/projects")
@RequiredArgsConstructor
public class MobileProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<Project> getProjects(Authentication authentication) {
        // Pour l'instant, on renvoie tous les projets. Plus tard, on pourra filtrer par utilisateur.
        return projectService.getAllProjects();
    }
}