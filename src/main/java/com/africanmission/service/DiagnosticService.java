package com.africanmission.service;

import com.africanmission.model.DiagnosticQuestion;
import com.africanmission.model.DiagnosticAnswer;
import com.africanmission.repository.DiagnosticQuestionRepository;
import com.africanmission.repository.DiagnosticAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosticService {

    private final DiagnosticQuestionRepository questionRepository;
    private final DiagnosticAnswerRepository answerRepository;

    // Questions
    @Transactional(readOnly = true)
    public List<DiagnosticQuestion> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<DiagnosticQuestion> getActiveQuestions() {
        return questionRepository.findByIsActiveTrueOrderByStepOrderAsc();
    }

    @Transactional(readOnly = true)
    public DiagnosticQuestion getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question non trouvée"));
    }

    @Transactional
    public DiagnosticQuestion saveQuestion(DiagnosticQuestion question) {
        return questionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    // Answers
    @Transactional(readOnly = true)
    public List<DiagnosticAnswer> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionIdOrderByDisplayOrderAsc(questionId);
    }

    @Transactional
    public DiagnosticAnswer saveAnswer(DiagnosticAnswer answer) {
        return answerRepository.save(answer);
    }

    @Transactional
    public void deleteAnswer(Long id) {
        answerRepository.deleteById(id);
    }
}