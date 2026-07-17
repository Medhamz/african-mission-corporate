package com.africanmission.repository;

import com.africanmission.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByIsActiveTrueOrderByDisplayOrderAsc();
    List<Project> findByCategoryAndIsActiveTrue(String category);
}