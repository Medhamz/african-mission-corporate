package com.africanmission.repository;

import com.africanmission.model.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {

    List<Faq> findByIsActiveTrueOrderByDisplayOrderAsc();

    List<Faq> findByCategoryAndIsActiveTrue(String category);

    @Query("SELECT DISTINCT f.category FROM Faq f WHERE f.isActive = true AND f.category IS NOT NULL")
    List<String> findDistinctCategoryByIsActiveTrue();
}