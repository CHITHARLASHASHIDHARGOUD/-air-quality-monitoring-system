package com.airquality.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for current air quality response
 * Matches frontend expected structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirQualityResponse {
    
    private Integer aqi;
    private String category;
    private String healthMessage;
    private Double pm25;
    private Double pm10;
    private Double co;
    private Double no2;
    private Double o3;
    private Double so2;
    private Double temperature;
    private Double humidity;
    private String time;  // HH:mm:ss format
    private String location;  // City name
}
