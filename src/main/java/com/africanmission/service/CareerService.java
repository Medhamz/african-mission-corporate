package com.africanmission.service;

import com.africanmission.model.Career;
import com.africanmission.repository.CareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerService {

    private final CareerRepository careerRepository;

    public List<Career> getActiveCareers() {
        return careerRepository.findByActiveTrue();
    }

    public List<Career> getAllCareers() {
        return careerRepository.findAll();
    }

    public Career save(Career career) {
        return careerRepository.save(career);
    }

    public void delete(Long id) {
        careerRepository.deleteById(id);
    }
}