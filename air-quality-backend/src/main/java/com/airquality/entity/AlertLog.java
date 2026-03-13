package com.airquality.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Alert Log Entity
 * Records when alerts are triggered for subscribers
 */
@Entity
@Table(name = "alert_logs", indexes = {
    @Index(name = "idx_sent_at", columnList = "sent_at"),
    @Index(name = "idx_subscriber_email", columnList = "subscriber_email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscriber_email", nullable = false)
    private String subscriberEmail;

    @Column(name = "subscriber_name", nullable = false)
    private String subscriberName;

    @Column(nullable = false)
    private Integer threshold;

    @Column(name = "current_aqi", nullable = false)
    private Integer currentAqi;

    @Column(nullable = false)
    private String category;

    @Column(name = "health_message", nullable = false, length = 500)
    private String healthMessage;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "email_sent")
    private Boolean emailSent = false;

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
        if (emailSent == null) {
            emailSent = false;
        }
    }
}
