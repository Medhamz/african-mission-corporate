package com.africanmission.repository;

import com.africanmission.model.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {
    List<Testimonial> findByIsApprovedTrueOrderByDisplayOrderAsc();
    List<Testimonial> findByIsApprovedFalseOrderByCreatedAtDesc();
}