package com.airquality.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for recent readings table
 * Simplified version with only required fields
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentReadingResponse {
    
    private String time;        // HH:mm:ss format
    private String recordedAt;  // ISO datetime format
    private Integer aqi;
    private String category;
    private Double pm25;
    private Double pm10;
    private Double co;
    private Double no2;
    private Double o3;
    private Double temperature;
    private Double humidity;
    private String location;
}
