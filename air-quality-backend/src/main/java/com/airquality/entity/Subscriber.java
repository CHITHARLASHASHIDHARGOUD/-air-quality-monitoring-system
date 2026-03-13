package com.airquality.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Subscriber Entity
 * Represents a citizen subscribed to air quality alerts
 */
@Entity
@Table(name = "subscribers", indexes = {
    @Index(name = "idx_email", columnList = "email", unique = true),
    @Index(name = "idx_threshold", columnList = "threshold"),
    @Index(name = "idx_city", columnList = "city")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private Integer threshold;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }
}
