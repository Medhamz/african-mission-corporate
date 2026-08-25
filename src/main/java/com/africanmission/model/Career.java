package com.africanmission.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "careers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String location;

    private String contractType; // CDD, CDI, Stage, etc.

    private String experience;

    private String badge;

    private String icon;

    private Integer displayOrder = 0;

    private Boolean isActive = true;

    private LocalDate createdAt = LocalDate.now();
}