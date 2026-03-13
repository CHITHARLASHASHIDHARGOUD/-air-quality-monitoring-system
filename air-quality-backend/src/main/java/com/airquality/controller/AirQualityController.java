package com.airquality.controller;

import com.airquality.dto.AirQualityResponse;
import com.airquality.dto.ApiResponse;
import com.airquality.dto.RecentReadingResponse;
import com.airquality.entity.AirQuality;
import com.airquality.service.AirPollutionService;
import com.airquality.service.AirQualityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * REST Controller for Air Quality endpoints
 */
@RestController
@RequestMapping("/api/air-quality")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")  // Can be restricted in production
public class AirQualityController {

    private final AirQualityService airQualityService;
    private final AirPollutionService airPollutionService;
    private final com.airquality.service.AlertService alertService;

    /**
     * GET /api/air-quality/current
     * Returns the most recent air quality reading
     * 
     * Response format matches frontend expectation:
     * {
     *   "aqi": 120,
     *   "category": "Unhealthy",
     *   "healthMessage": "Reduce outdoor activities",
     *   "pm25": 55.2,
     *   "pm10": 80.3,
     *   "co": 1.5,
     *   "no2": 60.0,
     *   "o3": 70.0,
     *   "temperature": 32.0,
     *   "humidity": 65.0,
     *   "time": "10:45:21"
     * }
     */
    @GetMapping("/current")
    public ResponseEntity<AirQualityResponse> getCurrentAirQuality() {
        log.info("GET /api/air-quality/current - Fetching current air quality");
        
        AirQualityResponse response = airQualityService.getCurrentAirQuality();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/air-quality/recent
     * Returns last 12 readings for the table display
     * 
     * Response format:
     * [
     *   {
     *     "time": "10:45:21",
     *     "aqi": 120,
     *     "pm25": 55.2,
     *     "pm10": 80.3,
     *     "temperature": 32.0,
     *     "humidity": 65.0
     *   },
     *   ...
     * ]
     */
    @GetMapping("/recent")
    public ResponseEntity<List<RecentReadingResponse>> getRecentReadings(
            @RequestParam(required = false) String city) {
        log.info("GET /api/air-quality/recent - Fetching recent readings for city={}", city);

        List<RecentReadingResponse> readings = StringUtils.hasText(city)
                ? airQualityService.getRecentReadingsByCity(city.trim())
                : airQualityService.getRecentReadings();
        return ResponseEntity.ok(readings);
    }

    /**
     * GET /api/air-quality/history/{city}
     * Returns complete AQI history for a city (ordered by latest first)
     */
    @GetMapping("/history/{city}")
    public ResponseEntity<List<RecentReadingResponse>> getHistoryByCity(@PathVariable String city) {
        log.info("GET /api/air-quality/history/{} - Fetching history", city);
        return ResponseEntity.ok(airQualityService.getHistoryByCity(city.trim()));
    }

    /**
     * GET /api/air-quality/{city}
     * Compatibility route for fetching real AQI by city
     */
    @GetMapping("/{city}")
    public ResponseEntity<AirQualityResponse> getAirQualityByCityAlias(@PathVariable String city) {
        log.info("GET /api/air-quality/{} - Fetching real air quality data", city);
        AirQualityResponse response = airPollutionService.getAirQualityByCity(city.trim());
        AirQuality saved = airQualityService.saveReadingFromResponse(response, city.trim());
        alertService.checkAndSendAlerts(saved);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/air-quality/readings/{city}
     * Returns latest 10 AQI readings for a city
     */
    @GetMapping("/readings/{city}")
    public ResponseEntity<List<RecentReadingResponse>> getReadingsByCity(@PathVariable String city) {
        log.info("GET /api/air-quality/readings/{} - Fetching latest readings", city);
        return ResponseEntity.ok(airQualityService.getTopReadingsByCity(city.trim()));
    }

    /**
     * POST /api/air-quality
     * Persist an AQI reading
     */
    @PostMapping
    public ResponseEntity<AirQuality> createAirQualityReading(@RequestBody AirQuality data) {
        log.info("POST /api/air-quality - Saving AQI reading for location={}", data.getLocation());
        AirQuality saved = airQualityService.saveReading(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * GET /api/air-quality/city/{cityName}
     * Returns real air quality data from OpenWeatherMap API for a specific city
     * 
     * @param cityName Name of the city (e.g., "Hyderabad", "London", "Paris")
     * @return Real air quality data including AQI, PM2.5, PM10, CO, NO2, O3, etc.
     */
    @GetMapping("/city/{cityName}")
    public ResponseEntity<AirQualityResponse> getAirQualityByCity(@PathVariable String cityName) {
        log.info("GET /api/air-quality/city/{} - Fetching real air quality data", cityName);
        
        AirQualityResponse response = airPollutionService.getAirQualityByCity(cityName);
        AirQuality saved = airQualityService.saveReadingFromResponse(response, cityName);
        alertService.checkAndSendAlerts(saved);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/air-quality/simulate/{cityName}
     * Returns simulated air quality data for a specific city
     * Useful for demo purposes or when API is unavailable
     * 
     * @param cityName Name of the city (e.g., "Hyderabad", "London", "Paris")
     * @return Simulated air quality data with realistic variations
     */
    @GetMapping("/simulate/{cityName}")
    public ResponseEntity<AirQualityResponse> getSimulatedAirQualityByCity(@PathVariable String cityName) {
        log.info("GET /api/air-quality/simulate/{} - Generating simulated air quality data", cityName);
        
        AirQualityResponse response = airQualityService.generateSimulatedDataForCity(cityName);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/air-quality/records
     * Delete all air quality records from the database
     */
    @DeleteMapping("/records")
    public ResponseEntity<ApiResponse<String>> deleteAllRecords() {
        log.info("DELETE /api/air-quality/records - Deleting all air quality records");
        airQualityService.deleteAllRecords();
        return ResponseEntity.ok(ApiResponse.success("All air quality records have been deleted"));
    }

    /**
     * GET /api/air-quality/health
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Air Quality API is running"));
    }
}
