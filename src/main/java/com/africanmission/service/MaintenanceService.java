package com.africanmission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final SiteSettingService siteSettingService;

    public boolean isMaintenanceMode() {
        String value = siteSettingService.getSettingValue("maintenance_mode");
        return "true".equalsIgnoreCase(value);
    }

    public void enableMaintenance() {
        siteSettingService.saveSetting("maintenance_mode", "true", "Mode maintenance activé");
    }

    public void disableMaintenance() {
        siteSettingService.saveSetting("maintenance_mode", "false", "Mode maintenance désactivé");
    }
}