package com.africanmission.service;

import com.africanmission.model.Activity;
import com.africanmission.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    public List<Activity> getAllActiveActivities() {
        return activityRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    public List<Activity> getActivitiesByCategory(String category) {
        return activityRepository.findByCategoryAndIsActiveTrue(category);
    }

    public Activity getActivityById(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
    }

    public Activity saveActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    public List<String> getAllCategories() {
        return activityRepository.findAll().stream()
                .map(Activity::getCategory)
                .distinct()
                .filter(category -> category != null && !category.isEmpty())
                .toList();
    }
}