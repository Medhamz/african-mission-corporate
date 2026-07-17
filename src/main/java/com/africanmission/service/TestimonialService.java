package com.africanmission.service;

import com.africanmission.model.Testimonial;
import com.africanmission.repository.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestimonialService {

    private final TestimonialRepository testimonialRepository;

    public Testimonial save(Testimonial testimonial) {
        return testimonialRepository.save(testimonial);
    }

    public List<Testimonial> getApprovedTestimonials() {
        return testimonialRepository.findByIsApprovedTrueOrderByDisplayOrderAsc();
    }

    public List<Testimonial> getPendingTestimonials() {
        return testimonialRepository.findByIsApprovedFalseOrderByCreatedAtDesc();
    }

    public List<Testimonial> getAllTestimonials() {
        return testimonialRepository.findAll();
    }

    public Testimonial getById(Long id) {
        return testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Témoignage non trouvé"));
    }

    public void delete(Long id) {
        testimonialRepository.deleteById(id);
    }

    public Testimonial approve(Long id) {
        Testimonial testimonial = getById(id);
        testimonial.setIsApproved(true);
        return testimonialRepository.save(testimonial);
    }

    public long countPending() {
        return testimonialRepository.findByIsApprovedFalseOrderByCreatedAtDesc().size();
    }
}