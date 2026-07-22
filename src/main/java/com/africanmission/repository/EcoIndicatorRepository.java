package com.africanmission.repository;

import com.africanmission.model.EcoIndicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EcoIndicatorRepository extends JpaRepository<EcoIndicator, Long> {
    Optional<EcoIndicator> findByCode(String code);
    List<EcoIndicator> findByIsActiveTrueOrderByCodeAsc();
}