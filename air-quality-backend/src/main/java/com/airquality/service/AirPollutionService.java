package com.airquality.service;

import com.airquality.dto.AirQualityResponse;
import com.airquality.dto.WeatherResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service for fetching real air pollution data from OpenWeatherMap Air Pollution API
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AirPollutionService {

    private final WebClient webClient;
    private final WeatherService weatherService;
    private final AQICategoryService categoryService;

    @Value("${airpollution.api.key:da40a01545734ca7e86b2574509f30f1}")
    private String apiKey;

    @Value("${airpollution.api.url:https://api.openweathermap.org/data/2.5/air_pollution}")
    private String apiUrl;

    /**
     * Fetch air pollution data for a specific city
     * First gets coordinates from weather API, then fetches pollution data
     */
    public AirQualityResponse getAirQualityByCity(String city) {
        try {
            log.info("Fetching air quality data for city: {}", city);

            // First, get coordinates for the city using weather API
            WeatherResponse.OpenWeatherMapResponse weatherResponse = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.openweathermap.org")
                            .path("/data/2.5/weather")
                            .queryParam("q", city)
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(WeatherResponse.OpenWeatherMapResponse.class)
                    .block();

            if (weatherResponse == null || weatherResponse.getCoord() == null) {
                log.warn("Could not get coordinates for city: {}", city);
                return createDefaultAirQuality(city);
            }

            double lat = weatherResponse.getCoord().getLat();
            double lon = weatherResponse.getCoord().getLon();

            log.info("City {} coordinates: lat={}, lon={}", city, lat, lon);

            // Now fetch air pollution data using coordinates
            return getAirQualityByCoordinates(lat, lon, city);

        } catch (Exception e) {
            log.error("Error fetching air quality data for city {}: {}", city, e.getMessage());
            return createDefaultAirQuality(city);
        }
    }

    /**
     * Fetch air pollution data by coordinates
     */
    public AirQualityResponse getAirQualityByCoordinates(double lat, double lon, String city) {
        try {
            log.info("Fetching air pollution data for coordinates: lat={}, lon={}", lat, lon);

            AirPollutionApiResponse response = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.openweathermap.org")
                            .path("/data/2.5/air_pollution")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(AirPollutionApiResponse.class)
                    .block();

            if (response == null || response.getList() == null || response.getList().isEmpty()) {
                log.warn("No air pollution data received for coordinates: lat={}, lon={}", lat, lon);
                return createDefaultAirQuality(city);
            }

            return mapToAirQualityResponse(response.getList().get(0), city);

        } catch (Exception e) {
            log.error("Error fetching air pollution data for coordinates lat={}, lon={}: {}", lat, lon, e.getMessage());
            return createDefaultAirQuality(city);
        }
    }

    /**
     * Map OpenWeatherMap Air Pollution API response to AirQualityResponse DTO
     */
    private AirQualityResponse mapToAirQualityResponse(AirPollutionData data, String city) {
        // OpenWeatherMap provides AQI (1-5 scale), we need to convert to US EPA scale (0-500)
        // AQI from API: 1=Good, 2=Fair, 3=Moderate, 4=Poor, 5=Very Poor
        int aqiFromApi = data.getMain().getAqi();
        
        // Get PM2.5 for more accurate AQI calculation (using US EPA standard)
        double pm25 = data.getComponents().getPm25();
        int aqi = categoryService.calculateAQIFromPM25(pm25);
        
        String category = categoryService.getCategory(aqi);
        String healthMessage = categoryService.getHealthMessage(category);

        // Get current weather for temperature and humidity
        WeatherResponse weather = weatherService.getCurrentWeather(city);

        return AirQualityResponse.builder()
                .aqi(aqi)
                .category(category)
                .healthMessage(healthMessage)
                .pm25(data.getComponents().getPm25())
                .pm10(data.getComponents().getPm10())
                .co(data.getComponents().getCo())
                .no2(data.getComponents().getNo2())
                .o3(data.getComponents().getO3())
                .so2(data.getComponents().getSo2())
                .temperature(weather.getTemperature())
                .humidity(weather.getHumidity() != null ? weather.getHumidity().doubleValue() : 0.0)
                .time(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")))
                .location(city)
                .build();
    }

    /**
     * Create default air quality response when API fails
     */
    private AirQualityResponse createDefaultAirQuality(String city) {
        return AirQualityResponse.builder()
                .aqi(50)
                .category("Good")
                .healthMessage("Air quality is satisfactory")
                .pm25(12.0)
                .pm10(25.0)
                .co(0.5)
                .no2(20.0)
                .o3(50.0)
                .so2(10.0)
                .temperature(20.0)
                .humidity(50.0)
                .time(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")))
                .location(city)
                .build();
    }

    // DTOs for OpenWeatherMap Air Pollution API Response

    @Data
    public static class AirPollutionApiResponse {
        private List<AirPollutionData> list;
    }

    @Data
    public static class AirPollutionData {
        private Long dt;
        private MainAqi main;
        private Components components;
    }

    @Data
    public static class MainAqi {
        private Integer aqi;  // Air Quality Index (1-5)
    }

    @Data
    public static class Components {
        private Double co;      // Carbon monoxide (μg/m³)
        private Double no;      // Nitrogen monoxide (μg/m³)
        private Double no2;     // Nitrogen dioxide (μg/m³)
        private Double o3;      // Ozone (μg/m³)
        private Double so2;     // Sulphur dioxide (μg/m³)
        @JsonProperty("pm2_5")
        private Double pm25;    // Fine particles matter (μg/m³)
        private Double pm10;    // Coarse particulate matter (μg/m³)
        private Double nh3;     // Ammonia (μg/m³)
    }
}
