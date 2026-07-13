package com.africanmission.repository;

import com.africanmission.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findByIsReadFalseOrderByCreatedAtDesc();
}