package com.africanmission.repository;

import com.africanmission.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Notifications non lues (pour le menu rapide / dropdown)
    List<Notification> findTop10ByIsReadFalseAndIsDismissedFalseOrderByCreatedAtDesc();
    List<Notification> findByIsReadFalseAndIsDismissedFalseOrderByCreatedAtDesc();

    // Notifications actives globales (lues + non lues, exclut les masquées/supprimées)
    List<Notification> findByIsDismissedFalseOrderByCreatedAtDesc();

    // Compteur de notifications non lues (pour les badges)
    long countByIsReadFalseAndIsDismissedFalse();
}