package com.africanmission.service;

import com.africanmission.model.Project;
import com.africanmission.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getActiveProjects() {
        return projectRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    public List<Project> getProjectsByCategory(String category) {
        return projectRepository.findByCategoryAndIsActiveTrue(category);
    }

    public Project getById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    public List<String> getAllCategories() {
        return projectRepository.findAll().stream()
                .map(Project::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .distinct()
                .toList();
    }
}