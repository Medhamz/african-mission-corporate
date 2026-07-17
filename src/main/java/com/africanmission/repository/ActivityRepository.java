package com.africanmission.repository;

import com.africanmission.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByIsActiveTrueOrderByDisplayOrderAsc();
    List<Activity> findByCategoryAndIsActiveTrue(String category);

    List<Activity> findByTitleContainingIgnoreCase(String query);
}