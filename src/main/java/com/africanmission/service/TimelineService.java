package com.africanmission.service;

import com.africanmission.model.TimelineEvent;
import com.africanmission.repository.TimelineEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimelineService {

    private final TimelineEventRepository timelineEventRepository;

    @Transactional(readOnly = true)
    public List<TimelineEvent> getAllEvents() {
        return timelineEventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TimelineEvent> getActiveEvents() {
        return timelineEventRepository.findByIsActiveTrueOrderByYearAscDisplayOrderAsc();
    }

    @Transactional(readOnly = true)
    public TimelineEvent getEventById(Long id) {
        return timelineEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
    }

    @Transactional
    public TimelineEvent saveEvent(TimelineEvent event) {
        return timelineEventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        timelineEventRepository.deleteById(id);
    }

    @Transactional
    public void toggleActive(Long id) {
        TimelineEvent event = getEventById(id);
        event.setIsActive(!event.getIsActive());
        timelineEventRepository.save(event);
    }
}