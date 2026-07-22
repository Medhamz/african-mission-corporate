package com.africanmission.repository;

import com.africanmission.model.TimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimelineEventRepository extends JpaRepository<TimelineEvent, Long> {
    List<TimelineEvent> findByIsActiveTrueOrderByYearAscDisplayOrderAsc();
    List<TimelineEvent> findByYear(String year);
}