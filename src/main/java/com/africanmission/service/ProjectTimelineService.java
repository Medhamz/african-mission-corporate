package com.africanmission.service;

import com.africanmission.model.ProjectTimeline;
import com.africanmission.repository.ProjectTimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectTimelineService {

    private final ProjectTimelineRepository projectTimelineRepository;

    @Transactional(readOnly = true)
    public List<ProjectTimeline> getAllProjects() {
        return projectTimelineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ProjectTimeline> getActiveProjects() {
        return projectTimelineRepository.findByIsActiveTrueOrderByStartDateAsc();
    }

    @Transactional(readOnly = true)
    public ProjectTimeline getProjectById(Long id) {
        return projectTimelineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
    }

    @Transactional
    public ProjectTimeline saveProject(ProjectTimeline project) {
        return projectTimelineRepository.save(project);
    }

    @Transactional
    public void deleteProject(Long id) {
        projectTimelineRepository.deleteById(id);
    }

    @Transactional
    public void toggleActive(Long id) {
        ProjectTimeline project = getProjectById(id);
        project.setIsActive(!project.getIsActive());
        projectTimelineRepository.save(project);
    }
}