package com.airquality.service;

import com.airquality.dto.AirQualityResponse;
import com.airquality.dto.RecentReadingResponse;
import com.airquality.entity.AirQuality;
import com.airquality.exception.ResourceNotFoundException;
import com.airquality.repository.AirQualityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Air Quality business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AirQualityService {

    private final AirQualityRepository airQualityRepository;
    private final AQICategoryService categoryService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Get current (most recent) air quality reading
     */
    public AirQualityResponse getCurrentAirQuality() {
        log.debug("Fetching current air quality data");
        
        AirQuality latest = airQualityRepository.findFirstByOrderByRecordedAtDesc()
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No air quality data available. Please wait for data collection to begin."));

        return mapToResponse(latest);
    }

    /**
     * Get recent readings (last 12)
     */
    public List<RecentReadingResponse> getRecentReadings() {
        log.debug("Fetching recent air quality readings");
        
        List<AirQuality> recentData = airQualityRepository.findTop12ByOrderByRecordedAtDesc();
        
        if (recentData.isEmpty()) {
            log.warn("No recent readings found");
        }

        return recentData.stream()
                .map(this::mapToRecentReading)
                .collect(Collectors.toList());
    }

    /**
     * Get recent readings (last 12) for a specific city/location
     */
    public List<RecentReadingResponse> getRecentReadingsByCity(String cityName) {
        log.debug("Fetching recent air quality readings for city={}", cityName);

        List<AirQuality> recentData = airQualityRepository
                .findTop12ByLocationIgnoreCaseOrderByRecordedAtDesc(cityName);

        if (recentData.isEmpty()) {
            log.warn("No recent readings found for city={}", cityName);
        }

        return recentData.stream()
                .map(this::mapToRecentReading)
                .collect(Collectors.toList());
    }

    /**
     * Get complete reading history for a specific city/location
     */
    public List<RecentReadingResponse> getHistoryByCity(String cityName) {
        log.debug("Fetching complete air quality history for city={}", cityName);

        List<AirQuality> historyData = airQualityRepository
                .findByLocationIgnoreCaseOrderByRecordedAtDesc(cityName);

        if (historyData.isEmpty()) {
            log.warn("No historical readings found for city={}", cityName);
        }

        return historyData.stream()
                .map(this::mapToRecentReading)
                .collect(Collectors.toList());
    }

        /**
         * Get latest 10 readings for a specific city/location
         */
        public List<RecentReadingResponse> getTopReadingsByCity(String cityName) {
        log.debug("Fetching latest 10 air quality readings for city={}", cityName);

        List<AirQuality> recentData = airQualityRepository
            .findTop10ByCityOrderByRecordedAtDesc(cityName);

        return recentData.stream()
            .map(this::mapToRecentReading)
            .collect(Collectors.toList());
        }

    /**
     * Save new air quality reading
     */
    @Transactional
    public AirQuality saveAirQualityReading(AirQuality airQuality) {
        log.debug("Saving new air quality reading: AQI={}", airQuality.getAqi());
        
        // Ensure category and health message are set
        if (airQuality.getCategory() == null || airQuality.getCategory().isEmpty()) {
            airQuality.setCategory(categoryService.getCategory(airQuality.getAqi()));
        }
        
        if (airQuality.getHealthMessage() == null || airQuality.getHealthMessage().isEmpty()) {
            airQuality.setHealthMessage(categoryService.getHealthMessage(airQuality.getCategory()));
        }

        return airQualityRepository.save(airQuality);
    }

    /**
     * Compatibility method name for save flow
     */
    @Transactional
    public AirQuality saveReading(AirQuality data) {
        return saveAirQualityReading(data);
    }

    /**
     * Persist an API response as an AirQuality entity
     */
    @Transactional
    public AirQuality saveReadingFromResponse(AirQualityResponse response, String requestedCity) {
        AirQuality reading = AirQuality.builder()
                .aqi(response.getAqi())
                .category(response.getCategory())
                .healthMessage(response.getHealthMessage())
                .pm25(response.getPm25() != null ? response.getPm25() : 0.0)
                .pm10(response.getPm10() != null ? response.getPm10() : 0.0)
                .co(response.getCo() != null ? response.getCo() : 0.0)
                .no2(response.getNo2() != null ? response.getNo2() : 0.0)
                .o3(response.getO3() != null ? response.getO3() : 0.0)
                .so2(response.getSo2())
                .temperature(response.getTemperature() != null ? response.getTemperature() : 0.0)
                .humidity(response.getHumidity() != null ? response.getHumidity() : 0.0)
                .location((response.getLocation() != null && !response.getLocation().isBlank())
                        ? response.getLocation()
                        : requestedCity)
                .recordedAt(LocalDateTime.now())
                .build();

        return saveAirQualityReading(reading);
    }

    /**
     * Delete all air quality records
     */
    @Transactional
    public void deleteAllRecords() {
        log.info("Deleting all air quality records");
        airQualityRepository.deleteAll();
    }

    /**
     * Clean up old records (keep only last N records)
     */
    @Transactional
    public void cleanupOldRecords(int maxRecords) {
        long count = airQualityRepository.count();
        
        if (count > maxRecords) {
            int recordsToDelete = (int) (count - maxRecords);
            log.info("Cleaning up {} old air quality records (keeping last {})", 
                     recordsToDelete, maxRecords);
            
            // This is a simplified cleanup - delete oldest records
            List<AirQuality> allRecords = airQualityRepository.findAll();
            allRecords.sort((a, b) -> a.getRecordedAt().compareTo(b.getRecordedAt()));
            
            List<AirQuality> recordsToRemove = allRecords.subList(0, recordsToDelete);
            airQualityRepository.deleteAll(recordsToRemove);
        }
    }

    /**
     * Map entity to response DTO
     */
    private AirQualityResponse mapToResponse(AirQuality entity) {
        return AirQualityResponse.builder()
                .aqi(entity.getAqi())
                .category(entity.getCategory())
                .healthMessage(entity.getHealthMessage())
                .pm25(entity.getPm25())
                .pm10(entity.getPm10())
                .co(entity.getCo())
                .no2(entity.getNo2())
                .o3(entity.getO3())
                .so2(entity.getSo2())
                .temperature(entity.getTemperature())
                .humidity(entity.getHumidity())
                .time(entity.getRecordedAt().format(TIME_FORMATTER))
                .location(entity.getLocation())
                .build();
    }

    /**
     * Map entity to recent reading DTO
     */
    private RecentReadingResponse mapToRecentReading(AirQuality entity) {
        return RecentReadingResponse.builder()
                .time(entity.getRecordedAt().format(TIME_FORMATTER))
                .recordedAt(entity.getRecordedAt().format(DATETIME_FORMATTER))
                .aqi(entity.getAqi())
                .category(entity.getCategory())
                .pm25(entity.getPm25())
                .pm10(entity.getPm10())
                .co(entity.getCo())
                .no2(entity.getNo2())
                .o3(entity.getO3())
                .temperature(entity.getTemperature())
                .humidity(entity.getHumidity())
                .location(entity.getLocation())
                .build();
    }

    /**
     * Generate simulated air quality data for a specific city
     * Useful for demo purposes or when real API is unavailable
     * 
     * @param cityName Name of the city
     * @return Simulated air quality response with realistic variations
     */
    public AirQualityResponse generateSimulatedDataForCity(String cityName) {
        log.info("Generating simulated air quality data for city: {}", cityName);
        
        // Generate realistic variations based on city name hash (consistent for same city)
        int seed = Math.abs(cityName.hashCode());
        java.util.Random random = new java.util.Random(seed + System.currentTimeMillis() / 10000);
        
        // Base values with city-specific variations
        double basePM25 = 20 + (seed % 50); // 20-70 base range
        double pm25 = basePM25 + (random.nextDouble() * 30 - 15); // ±15 variation
        pm25 = Math.max(5, Math.min(200, pm25)); // Keep in realistic range
        
        // Calculate AQI from PM2.5
        int aqi = categoryService.calculateAQIFromPM25(pm25);
        String category = categoryService.getCategory(aqi);
        String healthMessage = categoryService.getHealthMessage(category);
        
        // Generate correlated pollutants
        double pm10 = pm25 * (1.5 + random.nextDouble() * 0.5); // PM10 usually 1.5-2x PM2.5
        double co = 0.5 + random.nextDouble() * 2.5; // 0.5-3.0 ppm
        double no2 = 15 + random.nextDouble() * 60; // 15-75 ppb
        double o3 = 30 + random.nextDouble() * 70; // 30-100 ppb
        double so2 = 5 + random.nextDouble() * 30; // 5-35 μg/m³
        
        // Weather data (simulated)
        double temperature = 15 + random.nextDouble() * 20; // 15-35°C
        double humidity = 30 + random.nextDouble() * 50; // 30-80%
        
        return AirQualityResponse.builder()
                .aqi(aqi)
                .category(category)
                .healthMessage(healthMessage)
                .pm25(round(pm25, 1))
                .pm10(round(pm10, 1))
                .co(round(co, 2))
                .no2(round(no2, 1))
                .o3(round(o3, 1))
                .so2(round(so2, 1))
                .temperature(round(temperature, 1))
                .humidity(round(humidity, 1))
                .time(java.time.LocalDateTime.now().format(TIME_FORMATTER))
                .location(cityName + " (Simulated)")
                .build();
    }
    
    /**
     * Round double to specified decimal places
     */
    private double round(double value, int decimalPlaces) {
        double scale = Math.pow(10, decimalPlaces);
        return Math.round(value * scale) / scale;
    }
}
