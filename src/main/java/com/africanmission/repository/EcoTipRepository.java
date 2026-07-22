package com.africanmission.repository;

import com.africanmission.model.EcoTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoTipRepository extends JpaRepository<EcoTip, Long> {
    List<EcoTip> findByIsActiveTrueOrderByDisplayOrderAsc();
}