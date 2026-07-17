package com.africanmission.repository;

import com.africanmission.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop10ByIsReadFalseAndIsDismissedFalseOrderByCreatedAtDesc();
    List<Notification> findByIsReadFalseAndIsDismissedFalseOrderByCreatedAtDesc();
    long countByIsReadFalseAndIsDismissedFalse();
}