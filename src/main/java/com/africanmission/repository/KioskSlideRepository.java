package com.africanmission.repository;

import com.africanmission.model.KioskSlide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KioskSlideRepository extends JpaRepository<KioskSlide, Long> {

    List<KioskSlide> findByIsActiveTrueOrderBySlideOrderAsc();

    List<KioskSlide> findAllByOrderBySlideOrderAsc();
}