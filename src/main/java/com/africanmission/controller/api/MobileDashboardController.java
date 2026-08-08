package com.africanmission.controller.api;

import com.africanmission.model.*;
import com.africanmission.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile")
@RequiredArgsConstructor
public class MobileDashboardController {

    private final ProjectService projectService;
    private final ActivityService activityService;
    private final ContactService contactService;
    private final AdminUserService adminUserService;

    // Métriques du Dashboard Android
    @GetMapping("/dashboard/summary")
    public Map<String, Object> getDashboardSummary(Authentication authentication) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("projectsCount", projectService.getAllProjects().size());
        summary.put("activitiesCount", activityService.getAllActiveActivities().size());
        summary.put("pendingQuotesCount", contactService.getAllMessages().stream().filter(m -> !m.getIsRead()).count());
        return summary;
    }

    // Profil Utilisateur
    @GetMapping("/profile")
    public AdminUser getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        return adminUserService.getUserByUsername(username);
    }

    // Liste des activités
    @GetMapping("/activities")
    public List<Activity> getActivities() {
        return activityService.getAllActiveActivities();
    }
}