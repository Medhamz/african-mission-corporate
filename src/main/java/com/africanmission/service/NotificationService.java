package com.africanmission.service;

import com.africanmission.model.Notification;
import com.africanmission.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Crée et enregistre une notification système
     */
    public Notification createNotification(String title, String message, String type, String targetUrl) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type != null ? type : "info");
        notification.setTargetUrl(targetUrl);
        notification.setIsRead(false);
        notification.setIsDismissed(false);
        return notificationRepository.save(notification);
    }

    /**
     * Récupère toutes les notifications non supprimées (lues + non lues)
     */
    public List<Notification> getAllActiveNotifications() {
        return notificationRepository.findByIsDismissedFalseOrderByCreatedAtDesc();
    }

    /**
     * Récupère les notifications non lues pour le menu rapide
     */
    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findByIsReadFalseAndIsDismissedFalseOrderByCreatedAtDesc();
    }

    /**
     * Compte des notifications non lues pour les badges
     */
    public long getUnreadCount() {
        return notificationRepository.countByIsReadFalseAndIsDismissedFalse();
    }

    /**
     * Marquer une notification comme lue
     */
    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
            notificationRepository.save(n);
        });
    }

    /**
     * Supprimer une notification (masquer pour l'administrateur)
     */
    public void dismiss(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setIsDismissed(true);
            notificationRepository.save(n);
        });
    }

    /**
     * Tout marquer comme lu
     */
    public void markAllAsRead() {
        List<Notification> unread = notificationRepository.findByIsReadFalseAndIsDismissedFalseOrderByCreatedAtDesc();
        unread.forEach(n -> {
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unread);
    }

    /**
     * Tout supprimer / masquer
     */
    public void dismissAll() {
        List<Notification> active = notificationRepository.findByIsDismissedFalseOrderByCreatedAtDesc();
        active.forEach(n -> n.setIsDismissed(true));
        notificationRepository.saveAll(active);
    }
}