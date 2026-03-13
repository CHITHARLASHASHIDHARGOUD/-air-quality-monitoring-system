package com.airquality.service;

import com.airquality.dto.AirQualityResponse;
import com.airquality.entity.AirQuality;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Service for simulating real-time air quality data
 * This will be replaced with actual API integration later
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DataSimulatorService {

    private final AirQualityService airQualityService;
    private final AQICategoryService categoryService;
    private final AlertService alertService;
    private final AirPollutionService airPollutionService;

    @Value("${airquality.simulation.enabled:true}")
    private boolean simulationEnabled;

    @Value("${airquality.simulation.max-records:100}")
    private int maxRecords;

    @Value("${airquality.collection.enabled:true}")
    private boolean cityCollectionEnabled;

    @Value("${airquality.collection.city:Hyderabad}")
    private String collectionCity;

    private final Random random = new Random();

    // State variables for realistic simulation
    private double currentPM25 = 35.0;
    private double currentPM10 = 55.0;
    private double currentCO = 1.2;
    private double currentNO2 = 45.0;
    private double currentO3 = 55.0;
    private double currentSO2 = 15.0;
    private double currentTemp = 28.0;
    private double currentHumidity = 60.0;

    /**
     * Generate and save simulated data every 30 seconds
     */
    @Scheduled(fixedDelayString = "${airquality.simulation.interval:30000}")
    public void generateSimulatedData() {
        if (!simulationEnabled) {
            return;
        }

        try {
            log.debug("Generating simulated air quality data...");

            // Generate realistic data with gradual changes
            updateSimulatedValues();

            // Calculate AQI from PM2.5 (primary pollutant)
            int aqi = categoryService.calculateAQIFromPM25(currentPM25);
            String category = categoryService.getCategory(aqi);
            String healthMessage = categoryService.getHealthMessage(category);

            // Create air quality reading
            AirQuality reading = AirQuality.builder()
                    .aqi(aqi)
                    .category(category)
                    .healthMessage(healthMessage)
                    .pm25(round(currentPM25, 1))
                    .pm10(round(currentPM10, 1))
                    .co(round(currentCO, 2))
                    .no2(round(currentNO2, 1))
                    .o3(round(currentO3, 1))
                    .so2(round(currentSO2, 1))
                    .temperature(round(currentTemp, 1))
                    .humidity(round(currentHumidity, 1))
                    .location("Simulated")
                    .recordedAt(LocalDateTime.now())
                    .build();

            // Save to database
            AirQuality saved = airQualityService.saveAirQualityReading(reading);
            log.info("Simulated data saved: AQI={}, Category={}, PM2.5={}", 
                     saved.getAqi(), saved.getCategory(), saved.getPm25());

            // Check if alerts need to be sent
            alertService.checkAndSendAlerts(saved);

            // Cleanup old records
            airQualityService.cleanupOldRecords(maxRecords);

        } catch (Exception e) {
            log.error("Error generating simulated data", e);
        }
    }

    /**
     * Collect and save real city air quality data every minute
     */
        @Scheduled(
            fixedDelayString = "${airquality.collection.interval:60000}",
            initialDelayString = "${airquality.collection.initial-delay:5000}"
        )
    public void collectCityAirQualityData() {
        if (!cityCollectionEnabled) {
            return;
        }

        try {
            log.debug("Collecting real air quality data for city={}...", collectionCity);

            AirQualityResponse cityData = airPollutionService.getAirQualityByCity(collectionCity);

            AirQuality reading = AirQuality.builder()
                    .aqi(cityData.getAqi())
                    .category(cityData.getCategory())
                    .healthMessage(cityData.getHealthMessage())
                    .pm25(cityData.getPm25())
                    .pm10(cityData.getPm10())
                    .co(cityData.getCo())
                    .no2(cityData.getNo2())
                    .o3(cityData.getO3())
                    .so2(cityData.getSo2())
                    .temperature(cityData.getTemperature())
                    .humidity(cityData.getHumidity())
                    .location(collectionCity)
                    .recordedAt(LocalDateTime.now())
                    .build();

            AirQuality saved = airQualityService.saveAirQualityReading(reading);
            log.info("City reading saved: city={}, AQI={}, PM2.5={}",
                    collectionCity, saved.getAqi(), saved.getPm25());

            alertService.checkAndSendAlerts(saved);
            airQualityService.cleanupOldRecords(maxRecords);

        } catch (Exception e) {
            log.error("Error collecting city air quality data for {}", collectionCity, e);
        }
    }

    /**
     * Update simulated values with realistic gradual changes
     */
    private void updateSimulatedValues() {
        // PM2.5: range 5-200, gradual changes
        currentPM25 = adjustValue(currentPM25, 5, 200, 5);
        
        // PM10: usually higher than PM2.5
        currentPM10 = adjustValue(currentPM10, 10, 300, 8);
        
        // CO: range 0.1-5.0
        currentCO = adjustValue(currentCO, 0.1, 5.0, 0.3);
        
        // NO2: range 10-150
        currentNO2 = adjustValue(currentNO2, 10, 150, 5);
        
        // O3: range 20-120
        currentO3 = adjustValue(currentO3, 20, 120, 5);
        
        // SO2: range 5-50
        currentSO2 = adjustValue(currentSO2, 5, 50, 3);
        
        // Temperature: range 15-40°C
        currentTemp = adjustValue(currentTemp, 15, 40, 2);
        
        // Humidity: range 30-90%
        currentHumidity = adjustValue(currentHumidity, 30, 90, 3);
    }

    /**
     * Adjust value gradually within bounds
     */
    private double adjustValue(double current, double min, double max, double maxChange) {
        double change = (random.nextDouble() - 0.5) * 2 * maxChange;
        double newValue = current + change;
        
        // Keep within bounds
        newValue = Math.max(min, Math.min(max, newValue));
        
        return newValue;
    }

    /**
     * Round to specified decimal places
     */
    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    /**
     * Generate spike in pollution (for testing)
     * Can be called manually from controller for demonstration
     */
    public void generatePollutionSpike() {
        log.info("Generating pollution spike for testing...");
        currentPM25 = 150 + random.nextDouble() * 50;  // Unhealthy range
        currentPM10 = 200 + random.nextDouble() * 80;
        currentCO = 3.0 + random.nextDouble();
        currentNO2 = 100 + random.nextDouble() * 40;
        currentO3 = 90 + random.nextDouble() * 30;
    }

    /**
     * Reset to good air quality (for testing)
     */
    public void resetToGoodQuality() {
        log.info("Resetting to good air quality...");
        currentPM25 = 20 + random.nextDouble() * 15;
        currentPM10 = 30 + random.nextDouble() * 20;
        currentCO = 0.5 + random.nextDouble() * 0.5;
        currentNO2 = 20 + random.nextDouble() * 15;
        currentO3 = 30 + random.nextDouble() * 15;
    }
}
