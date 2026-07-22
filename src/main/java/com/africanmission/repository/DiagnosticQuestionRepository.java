package com.africanmission.repository;

import com.africanmission.model.DiagnosticQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosticQuestionRepository extends JpaRepository<DiagnosticQuestion, Long> {
    List<DiagnosticQuestion> findByIsActiveTrueOrderByStepOrderAsc();
    List<DiagnosticQuestion> findByCategory(String category);
}