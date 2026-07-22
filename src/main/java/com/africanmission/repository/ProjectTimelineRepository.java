package com.africanmission.repository;

import com.africanmission.model.ProjectTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTimelineRepository extends JpaRepository<ProjectTimeline, Long> {
    List<ProjectTimeline> findByIsActiveTrueOrderByStartDateAsc();
    List<ProjectTimeline> findByStatus(String status);
}