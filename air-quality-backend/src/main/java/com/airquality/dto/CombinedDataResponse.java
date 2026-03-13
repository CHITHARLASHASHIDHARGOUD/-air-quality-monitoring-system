package com.airquality.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Combined air quality and weather data response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinedDataResponse {
    
    private AirQualityResponse airQuality;
    private WeatherResponse weather;
    private String location;
    private String timestamp;
    
}
