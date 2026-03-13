package com.airquality.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Air Quality Entity
 * Represents a single air quality reading with all pollutant measurements
 */
@Entity
@Table(name = "air_quality", indexes = {
    @Index(name = "idx_recorded_at", columnList = "recorded_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirQuality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer aqi;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "health_message", nullable = false, length = 500)
    private String healthMessage;

    @Column(name = "pm25", nullable = false)
    private Double pm25;

    @Column(name = "pm10", nullable = false)
    private Double pm10;

    @Column(nullable = false)
    private Double co;

    @Column(nullable = false)
    private Double no2;

    @Column(nullable = false)
    private Double o3;

    @Column
    private Double so2;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double humidity;

    @Column(length = 100)
    private String location;

    @Transient
    public String getCity() {
        return location;
    }

    public void setCity(String city) {
        this.location = city;
    }

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }
}
