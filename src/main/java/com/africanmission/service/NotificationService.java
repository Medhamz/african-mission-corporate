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

    public Notification createNotification(String title, String message, String type, String targetUrl) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setTargetUrl(targetUrl);
        notification.setIsRead(false);
        notification.setIsDismissed(false);
        return notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findTop10ByIsReadFalseAndIsDismissedFalseOrderByCreatedAtDesc();
    }

    public long getUnreadCount() {
        return notificationRepository.countByIsReadFalseAndIsDismissedFalse();
    }

    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public Notification dismiss(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        notification.setIsDismissed(true);
        return notificationRepository.save(notification);
    }

    public void dismissAll() {
        List<Notification> notifications = notificationRepository.findByIsReadFalseAndIsDismissedFalseOrderByCreatedAtDesc();
        notifications.forEach(n -> n.setIsDismissed(true));
        notificationRepository.saveAll(notifications);
    }

    public Notification markAllAsRead() {
        List<Notification> notifications = notificationRepository.findByIsReadFalseAndIsDismissedFalseOrderByCreatedAtDesc();
        notifications.forEach(n -> {
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(notifications);
        return null;
    }
}