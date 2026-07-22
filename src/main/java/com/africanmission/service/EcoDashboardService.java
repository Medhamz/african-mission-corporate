package com.africanmission.service;

import com.africanmission.model.EcoIndicator;
import com.africanmission.model.EcoGoal;
import com.africanmission.model.EcoTip;
import com.africanmission.repository.EcoIndicatorRepository;
import com.africanmission.repository.EcoGoalRepository;
import com.africanmission.repository.EcoTipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EcoDashboardService {

    private final EcoIndicatorRepository indicatorRepository;
    private final EcoGoalRepository goalRepository;
    private final EcoTipRepository tipRepository;

    // Indicators
    @Transactional(readOnly = true)
    public List<EcoIndicator> getAllIndicators() {
        return indicatorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public EcoIndicator getIndicatorById(Long id) {
        return indicatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Indicateur non trouvé"));
    }

    @Transactional
    public EcoIndicator saveIndicator(EcoIndicator indicator) {
        return indicatorRepository.save(indicator);
    }

    @Transactional
    public void deleteIndicator(Long id) {
        indicatorRepository.deleteById(id);
    }

    // Goals
    @Transactional(readOnly = true)
    public List<EcoGoal> getAllGoals() {
        return goalRepository.findAll();
    }

    @Transactional
    public EcoGoal saveGoal(EcoGoal goal) {
        return goalRepository.save(goal);
    }

    @Transactional
    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }

    // Tips
    @Transactional(readOnly = true)
    public List<EcoTip> getAllTips() {
        return tipRepository.findAll();
    }

    @Transactional
    public EcoTip saveTip(EcoTip tip) {
        return tipRepository.save(tip);
    }

    @Transactional
    public void deleteTip(Long id) {
        tipRepository.deleteById(id);
    }
}