package com.africanmission.repository;

import com.africanmission.model.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {

    Optional<Newsletter> findByEmailIgnoreCase(String email);

    List<Newsletter> findByIsActiveTrue();

    boolean existsByEmailIgnoreCase(String email);
}