package com.africanmission.repository;

import com.africanmission.model.DiagnosticAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosticAnswerRepository extends JpaRepository<DiagnosticAnswer, Long> {
    List<DiagnosticAnswer> findByQuestionIdOrderByDisplayOrderAsc(Long questionId);
}