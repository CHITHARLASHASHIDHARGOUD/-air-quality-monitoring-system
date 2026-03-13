package com.airquality.controller;

import com.airquality.dto.ApiResponse;
import com.airquality.dto.CombinedDataResponse;
import com.airquality.dto.WeatherResponse;
import com.airquality.service.AirQualityService;
import com.airquality.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * REST Controller for Weather API endpoints
 */
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class WeatherController {

    private final WeatherService weatherService;
    private final AirQualityService airQualityService;

    /**
     * GET /api/weather/current
     * Get current weather for default city
     * 
     * @return Current weather data
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<WeatherResponse>> getCurrentWeather() {
        log.info("GET /api/weather/current - Fetching current weather");

        WeatherResponse weather = weatherService.getCurrentWeather();

        return ResponseEntity.ok(
                ApiResponse.success("Weather data retrieved successfully", weather)
        );
    }

    /**
     * GET /api/weather/city/{cityName}
     * Get weather for specific city
     * 
     * @param cityName Name of the city
     * @return Weather data for the city
     */
    @GetMapping("/city/{cityName}")
    public ResponseEntity<ApiResponse<WeatherResponse>> getWeatherByCity(
            @PathVariable String cityName) {
        log.info("GET /api/weather/city/{} - Fetching weather for city", cityName);

        WeatherResponse weather = weatherService.getCurrentWeather(cityName);

        return ResponseEntity.ok(
                ApiResponse.success("Weather data retrieved for " + cityName, weather)
        );
    }

    /**
     * GET /api/weather/coordinates
     * Get weather by geographic coordinates
     * 
     * @param lat Latitude
     * @param lon Longitude
     * @return Weather data for the coordinates
     */
    @GetMapping("/coordinates")
    public ResponseEntity<ApiResponse<WeatherResponse>> getWeatherByCoordinates(
            @RequestParam Double lat,
            @RequestParam Double lon) {
        log.info("GET /api/weather/coordinates - Fetching weather for lat={}, lon={}", lat, lon);

        WeatherResponse weather = weatherService.getWeatherByCoordinates(lat, lon);

        return ResponseEntity.ok(
                ApiResponse.success("Weather data retrieved for coordinates", weather)
        );
    }

    /**
     * GET /api/weather/combined
     * Get combined air quality and weather data
     * 
     * @return Combined air quality and weather data
     */
    @GetMapping("/combined")
    public ResponseEntity<ApiResponse<CombinedDataResponse>> getCombinedData() {
        log.info("GET /api/weather/combined - Fetching combined air quality and weather data");

        var airQuality = airQualityService.getCurrentAirQuality();
        var weather = weatherService.getCurrentWeather();

        CombinedDataResponse combined = CombinedDataResponse.builder()
                .airQuality(airQuality)
                .weather(weather)
                .location(weather.getCity() + ", " + weather.getCountry())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        return ResponseEntity.ok(
                ApiResponse.success("Combined data retrieved successfully", combined)
        );
    }

    /**
     * GET /api/weather/combined/city/{cityName}
     * Get combined air quality and weather data for specific city
     * 
     * @param cityName Name of the city
     * @return Combined air quality and weather data
     */
    @GetMapping("/combined/city/{cityName}")
    public ResponseEntity<ApiResponse<CombinedDataResponse>> getCombinedDataByCity(
            @PathVariable String cityName) {
        log.info("GET /api/weather/combined/city/{} - Fetching combined data for city", cityName);

        var airQuality = airQualityService.getCurrentAirQuality();
        var weather = weatherService.getCurrentWeather(cityName);

        CombinedDataResponse combined = CombinedDataResponse.builder()
                .airQuality(airQuality)
                .weather(weather)
                .location(weather.getCity() + ", " + weather.getCountry())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        return ResponseEntity.ok(
                ApiResponse.success("Combined data retrieved for " + cityName, combined)
        );
    }
}
