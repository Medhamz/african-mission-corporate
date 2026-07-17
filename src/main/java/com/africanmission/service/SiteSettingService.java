package com.africanmission.service;

import com.africanmission.model.SiteSetting;
import com.africanmission.repository.SiteSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteSettingService {

    private final SiteSettingRepository siteSettingRepository;

    public SiteSetting getSetting(String key) {
        return siteSettingRepository.findByKey(key)
                .orElse(null);
    }

    public String getSettingValue(String key) {
        SiteSetting setting = getSetting(key);
        return setting != null ? setting.getValue() : null;
    }

    public SiteSetting saveSetting(String key, String value, String description) {
        SiteSetting setting = siteSettingRepository.findByKey(key)
                .orElse(new SiteSetting());
        setting.setKey(key);
        setting.setValue(value);
        setting.setDescription(description);
        return siteSettingRepository.save(setting);
    }

    public List<SiteSetting> getAllSettings() {
        return siteSettingRepository.findAll();
    }

    public Map<String, String> getSettingsMap() {
        return getAllSettings().stream()
                .collect(Collectors.toMap(SiteSetting::getKey, SiteSetting::getValue));
    }

    public void deleteSetting(String key) {
        siteSettingRepository.findByKey(key).ifPresent(siteSettingRepository::delete);
    }
}