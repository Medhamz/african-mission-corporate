package com.africanmission.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "kiosk_slides")
@Data
public class KioskSlideEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer slideOrder;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String content; // HTML ou JSON selon le type de slide

    @Column(length = 500)
    private String imageUrl;

    private String slideType; // "hero", "grid", "stats", "contact"

    @Column(nullable = false)
    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}