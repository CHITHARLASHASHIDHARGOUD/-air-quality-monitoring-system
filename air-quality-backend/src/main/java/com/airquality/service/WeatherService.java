package com.airquality.service;

import com.airquality.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Service for fetching weather data from OpenWeatherMap API
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final WebClient webClient;

    @Value("${weather.api.key:da40a01545734ca7e86b2574509f30f1}")
    private String apiKey;

    @Value("${weather.api.url:https://api.openweathermap.org/data/2.5/weather}")
    private String apiUrl;

    @Value("${weather.api.city:London}")
    private String defaultCity;

    @Value("${weather.api.units:metric}")
    private String units;

    /**
     * Fetch current weather data for default city
     */
    public WeatherResponse getCurrentWeather() {
        return getCurrentWeather(defaultCity);
    }

    /**
     * Fetch current weather data for specified city
     * @param city City name
     * @return Weather data
     */
    public WeatherResponse getCurrentWeather(String city) {
        try {
            log.info("Fetching weather data for city: {}", city);

            String url = String.format("%s?q=%s&appid=%s&units=%s", 
                    apiUrl, city, apiKey, units);

            log.debug("Weather API URL: {}", url);

            WeatherResponse.OpenWeatherMapResponse response = webClient
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(WeatherResponse.OpenWeatherMapResponse.class)
                    .block();

            if (response == null) {
                log.warn("No weather data received for city: {}", city);
                return createDefaultWeather(city);
            }

            log.info("Weather data retrieved successfully for {}: {}°C, {}", 
                    city, response.getMain().getTemp(), response.getWeather().get(0).getDescription());

            return mapToWeatherResponse(response);

        } catch (Exception e) {
            log.error("Error fetching weather data for city {}: {}", city, e.getMessage(), e);
            return createDefaultWeather(city);
        }
    }

    /**
     * Fetch weather data by coordinates
     * @param lat Latitude
     * @param lon Longitude
     * @return Weather data
     */
    public WeatherResponse getWeatherByCoordinates(Double lat, Double lon) {
        try {
            log.info("Fetching weather data for coordinates: lat={}, lon={}", lat, lon);

            String url = String.format("%s?lat=%s&lon=%s&appid=%s&units=%s", 
                    apiUrl, lat, lon, apiKey, units);

            WeatherResponse.OpenWeatherMapResponse response = webClient
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(WeatherResponse.OpenWeatherMapResponse.class)
                    .block();

            if (response == null) {
                log.warn("No weather data received for coordinates: lat={}, lon={}", lat, lon);
                return createDefaultWeather("Unknown");
            }

            return mapToWeatherResponse(response);

        } catch (Exception e) {
            log.error("Error fetching weather data for coordinates lat={}, lon={}: {}", lat, lon, e.getMessage());
            return createDefaultWeather("Unknown");
        }
    }

    /**
     * Map OpenWeatherMap response to WeatherResponse DTO
     */
    private WeatherResponse mapToWeatherResponse(WeatherResponse.OpenWeatherMapResponse response) {
        WeatherResponse.OpenWeatherMapResponse.Weather weather = 
                response.getWeather() != null && !response.getWeather().isEmpty() 
                        ? response.getWeather().get(0) 
                        : null;

        return WeatherResponse.builder()
                .city(response.getName())
                .country(response.getSys() != null ? response.getSys().getCountry() : "N/A")
                .temperature(response.getMain() != null ? response.getMain().getTemp() : null)
                .feelsLike(response.getMain() != null ? response.getMain().getFeelsLike() : null)
                .humidity(response.getMain() != null ? response.getMain().getHumidity() : null)
                .pressure(response.getMain() != null ? response.getMain().getPressure().doubleValue() : null)
                .windSpeed(response.getWind() != null ? response.getWind().getSpeed() : null)
                .windDegree(response.getWind() != null ? response.getWind().getDeg() : null)
                .description(weather != null ? weather.getDescription() : "N/A")
                .icon(weather != null ? weather.getIcon() : "01d")
                .visibility(response.getVisibility())
                .clouds(response.getClouds() != null ? response.getClouds().getAll() : null)
                .timestamp(response.getDt() != null 
                        ? LocalDateTime.ofInstant(Instant.ofEpochSecond(response.getDt()), ZoneId.systemDefault())
                        : LocalDateTime.now())
                .build();
    }

    /**
     * Create default weather response when API fails
     */
    private WeatherResponse createDefaultWeather(String city) {
        return WeatherResponse.builder()
                .city(city)
                .country("N/A")
                .temperature(20.0)
                .feelsLike(20.0)
                .humidity(50)
                .pressure(1013.0)
                .windSpeed(5.0)
                .windDegree(0)
                .description("Data unavailable")
                .icon("01d")
                .visibility(10000)
                .clouds(0)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
