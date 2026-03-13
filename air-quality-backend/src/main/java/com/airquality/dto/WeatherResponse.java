package com.airquality.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Weather data response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    
    private String city;
    private String country;
    private Double temperature;      // Celsius
    private Double feelsLike;        // Celsius
    private Integer humidity;        // Percentage
    private Double pressure;         // hPa
    private Double windSpeed;        // m/s
    private Integer windDegree;      // Degrees
    private String description;      // Weather description
    private String icon;             // Weather icon code
    private Integer visibility;      // Meters
    private Integer clouds;          // Cloudiness percentage
    private LocalDateTime timestamp;
    
    /**
     * OpenWeatherMap API response structure
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpenWeatherMapResponse {
        private Coord coord;
        private java.util.List<Weather> weather;
        private String base;
        private Main main;
        private Integer visibility;
        private Wind wind;
        private Clouds clouds;
        private Long dt;
        private Sys sys;
        private Integer timezone;
        private Long id;
        private String name;
        private Integer cod;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Coord {
            private Double lon;
            private Double lat;
        }
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Weather {
            private Integer id;
            private String main;
            private String description;
            private String icon;
        }
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Main {
            private Double temp;
            @JsonProperty("feels_like")
            private Double feelsLike;
            @JsonProperty("temp_min")
            private Double tempMin;
            @JsonProperty("temp_max")
            private Double tempMax;
            private Integer pressure;
            private Integer humidity;
            @JsonProperty("sea_level")
            private Integer seaLevel;
            @JsonProperty("grnd_level")
            private Integer grndLevel;
        }
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Wind {
            private Double speed;
            private Integer deg;
            private Double gust;
        }
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Clouds {
            private Integer all;
        }
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Sys {
            private Integer type;
            private Long id;
            private String country;
            private Long sunrise;
            private Long sunset;
        }
    }
}
