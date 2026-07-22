package com.africanmission.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "diagnostic_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String answer;

    @Column(nullable = false, length = 50)
    private String value;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private DiagnosticQuestion question;

    private Integer displayOrder = 0;

    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}