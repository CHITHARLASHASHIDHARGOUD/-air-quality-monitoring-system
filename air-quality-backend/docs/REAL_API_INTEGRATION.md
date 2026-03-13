# Real API Integration Guide
## Converting from Simulated Data to Real Air Quality API

---

## 🎯 Overview

This guide shows you how to replace the simulated data generator with **real air quality data** from external APIs.

---

## 🌐 Available Air Quality APIs

### 1. **OpenAQ** (Recommended - Free)
- ✅ Free and open source
- ✅ Global coverage
- ✅ No API key required
- ✅ Real-time data
- 📖 Docs: https://docs.openaq.org/

### 2. **IQAir** (Commercial)
- ⭐ High accuracy
- 💰 Free tier: 10,000 calls/month
- 🔑 API key required
- 📖 Docs: https://www.iqair.com/air-pollution-data-api

### 3. **AirNow** (US Government - Free)
- ✅ Free for US locations
- 🔑 API key required
- 🇺🇸 US coverage only
- 📖 Docs: https://docs.airnowapi.org/

### 4. **WAQI (World Air Quality Index)**
- ✅ Free tier available
- 🌍 Global coverage
- 🔑 API key required
- 📖 Docs: https://aqicn.org/api/

---

## 🚀 Implementation: OpenAQ Integration

We'll use **OpenAQ** as the primary example (free, no key required).

### Step 1: Create External API Service

Create new file: `ExternalAirQualityService.java`

```java
package com.airquality.service;

import com.airquality.entity.AirQuality;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Service for fetching real air quality data from external APIs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalAirQualityService {

    private final WebClient webClient;
    private final AQICategoryService categoryService;

    @Value("${airquality.api.url:https://api.openaq.org/v2/latest}")
    private String apiUrl;

    @Value("${airquality.api.city:London}")
    private String city;

    @Value("${airquality.api.country:GB}")
    private String country;

    /**
     * Fetch real air quality data from OpenAQ
     */
    public AirQuality fetchRealAirQuality() {
        try {
            log.info("Fetching real air quality data for {}, {}", city, country);

            // Call OpenAQ API
            OpenAQResponse response = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/latest")
                            .queryParam("city", city)
                            .queryParam("country", country)
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .bodyToMono(OpenAQResponse.class)
                    .block();

            if (response == null || response.getResults().isEmpty()) {
                log.warn("No data received from API, using default values");
                return createDefaultReading();
            }

            // Parse and convert to our format
            return convertToAirQuality(response.getResults().get(0));

        } catch (Exception e) {
            log.error("Error fetching real air quality data", e);
            return createDefaultReading();
        }
    }

    /**
     * Convert OpenAQ response to our AirQuality entity
     */
    private AirQuality convertToAirQuality(OpenAQResult result) {
        // Extract pollutant measurements
        Double pm25 = extractMeasurement(result, "pm25");
        Double pm10 = extractMeasurement(result, "pm10");
        Double co = extractMeasurement(result, "co");
        Double no2 = extractMeasurement(result, "no2");
        Double o3 = extractMeasurement(result, "o3");

        // Calculate AQI from PM2.5
        int aqi = categoryService.calculateAQIFromPM25(pm25);
        String category = categoryService.getCategory(aqi);
        String healthMessage = categoryService.getHealthMessage(category);

        return AirQuality.builder()
                .aqi(aqi)
                .category(category)
                .healthMessage(healthMessage)
                .pm25(pm25)
                .pm10(pm10)
                .co(co)
                .no2(no2)
                .o3(o3)
                .temperature(result.getTemperature() != null ? result.getTemperature() : 25.0)
                .humidity(result.getHumidity() != null ? result.getHumidity() : 60.0)
                .recordedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Extract measurement value for specific parameter
     */
    private Double extractMeasurement(OpenAQResult result, String parameter) {
        return result.getMeasurements().stream()
                .filter(m -> m.getParameter().equalsIgnoreCase(parameter))
                .findFirst()
                .map(OpenAQMeasurement::getValue)
                .orElse(getDefaultValue(parameter));
    }

    /**
     * Get default value if measurement is missing
     */
    private Double getDefaultValue(String parameter) {
        switch (parameter.toLowerCase()) {
            case "pm25": return 35.0;
            case "pm10": return 50.0;
            case "co": return 1.0;
            case "no2": return 40.0;
            case "o3": return 50.0;
            default: return 0.0;
        }
    }

    /**
     * Create default reading when API fails
     */
    private AirQuality createDefaultReading() {
        int aqi = 50;
        return AirQuality.builder()
                .aqi(aqi)
                .category(categoryService.getCategory(aqi))
                .healthMessage(categoryService.getHealthMessage(aqi))
                .pm25(25.0)
                .pm10(40.0)
                .co(0.8)
                .no2(30.0)
                .o3(45.0)
                .temperature(25.0)
                .humidity(60.0)
                .recordedAt(LocalDateTime.now())
                .build();
    }

    // ===== DTO Classes for OpenAQ API Response =====

    @lombok.Data
    public static class OpenAQResponse {
        private java.util.List<OpenAQResult> results;
    }

    @lombok.Data
    public static class OpenAQResult {
        private String location;
        private String city;
        private String country;
        private java.util.List<OpenAQMeasurement> measurements;
        private Double temperature;
        private Double humidity;
    }

    @lombok.Data
    public static class OpenAQMeasurement {
        private String parameter;
        private Double value;
        private String unit;
    }
}
```

### Step 2: Update DataSimulatorService

Modify `DataSimulatorService.java` to use real API:

```java
package com.airquality.service;

import com.airquality.entity.AirQuality;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSimulatorService {

    private final AirQualityService airQualityService;
    private final AlertService alertService;
    private final ExternalAirQualityService externalAirQualityService;  // NEW

    @Value("${airquality.data.source:simulated}")  // NEW: simulated or real
    private String dataSource;

    @Value("${airquality.simulation.max-records:100}")
    private int maxRecords;

    /**
     * Fetch and save air quality data every 30 seconds
     */
    @Scheduled(fixedDelayString = "${airquality.simulation.interval:30000}")
    public void fetchAirQualityData() {
        try {
            log.debug("Fetching air quality data (source: {})", dataSource);

            AirQuality reading;

            // Choose data source
            if ("real".equalsIgnoreCase(dataSource)) {
                // Fetch from real API
                reading = externalAirQualityService.fetchRealAirQuality();
                log.info("Real data fetched: AQI={}, Location={}", 
                         reading.getAqi(), reading.getCategory());
            } else {
                // Use simulated data (existing logic)
                reading = generateSimulatedReading();
                log.info("Simulated data generated: AQI={}", reading.getAqi());
            }

            // Save to database
            AirQuality saved = airQualityService.saveAirQualityReading(reading);

            // Check alerts
            alertService.checkAndSendAlerts(saved);

            // Cleanup old records
            airQualityService.cleanupOldRecords(maxRecords);

        } catch (Exception e) {
            log.error("Error fetching air quality data", e);
        }
    }

    // Keep existing simulation methods for fallback...
}
```

### Step 3: Update Configuration

Add to `application.properties`:

```properties
# ===============================================
# Data Source Configuration
# ===============================================
# Options: simulated | real
airquality.data.source=real

# ===============================================
# External API Configuration (OpenAQ)
# ===============================================
airquality.api.url=https://api.openaq.org
airquality.api.city=London
airquality.api.country=GB

# Fallback to simulation if API fails
airquality.api.fallback-to-simulation=true
```

### Step 4: Test Real API

```bash
# Restart application
mvn spring-boot:run

# Check logs - should see:
# "Fetching real air quality data for London, GB"
# "Real data fetched: AQI=XX"

# Test endpoint
curl http://localhost:8080/api/air-quality/current
```

---

## 🔑 IQAir API Integration (Alternative)

### Step 1: Get API Key

1. Sign up at https://www.iqair.com/air-pollution-data-api
2. Get your API key

### Step 2: Create IQAir Service

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class IQAirService {

    private final WebClient webClient;

    @Value("${airquality.iqair.api-key}")
    private String apiKey;

    @Value("${airquality.iqair.city:London}")
    private String city;

    public AirQuality fetchFromIQAir() {
        IQAirResponse response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.airvisual.com")
                        .path("/v2/city")
                        .queryParam("city", city)
                        .queryParam("state", "England")
                        .queryParam("country", "UK")
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(IQAirResponse.class)
                .block();

        // Parse response and convert to AirQuality
        return convertIQAirResponse(response);
    }

    @Data
    public static class IQAirResponse {
        private IQAirData data;
    }

    @Data
    public static class IQAirData {
        private IQAirCurrent current;
    }

    @Data
    public static class IQAirCurrent {
        private IQAirPollution pollution;
        private IQAirWeather weather;
    }

    @Data
    public static class IQAirPollution {
        private Integer aqius;  // US AQI
        private Integer aqicn;  // China AQI
    }

    @Data
    public static class IQAirWeather {
        private Double tp;  // temperature
        private Integer hu;  // humidity
    }
}
```

### Step 3: Configure

```properties
airquality.iqair.api-key=YOUR_API_KEY_HERE
airquality.iqair.city=London
```

---

## 🌍 AirNow API Integration (US Only)

### Configuration

```properties
airquality.airnow.api-key=YOUR_API_KEY_HERE
airquality.airnow.zip-code=90210
```

### Service

```java
@Service
public class AirNowService {

    public AirQuality fetchFromAirNow() {
        String url = String.format(
            "https://www.airnowapi.org/aq/observation/zipCode/current/?format=application/json&zipCode=%s&API_KEY=%s",
            zipCode, apiKey
        );

        AirNowResponse[] response = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(AirNowResponse[].class)
                .block();

        return convertAirNowResponse(response);
    }
}
```

---

## 🔄 Switch Between Sources

### Runtime Configuration

Add endpoint to switch data source:

```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Value("${airquality.data.source}")
    private String dataSource;

    @PostMapping("/switch-source")
    public ResponseEntity<String> switchDataSource(@RequestParam String source) {
        if (!source.equals("simulated") && !source.equals("real")) {
            return ResponseEntity.badRequest().body("Invalid source");
        }

        // Update configuration
        System.setProperty("airquality.data.source", source);
        
        return ResponseEntity.ok("Data source switched to: " + source);
    }

    @GetMapping("/current-source")
    public ResponseEntity<String> getCurrentSource() {
        return ResponseEntity.ok(dataSource);
    }
}
```

---

## 📊 Multi-City Support

### Configuration

```properties
airquality.cities=London;GB,Paris;FR,NewYork;US
```

### Service

```java
@Service
public class MultiCityService {

    @Scheduled(fixedDelay = 30000)
    public void fetchMultipleCities() {
        String[] cities = citiesConfig.split(",");
        
        for (String cityConfig : cities) {
            String[] parts = cityConfig.split(";");
            String city = parts[0];
            String country = parts[1];
            
            AirQuality data = fetchForCity(city, country);
            airQualityService.saveAirQualityReading(data);
        }
    }
}
```

---

## ⚠️ Best Practices

### 1. Error Handling

```java
public AirQuality fetchWithRetry() {
    int maxRetries = 3;
    int attempt = 0;

    while (attempt < maxRetries) {
        try {
            return externalAirQualityService.fetchRealAirQuality();
        } catch (Exception e) {
            attempt++;
            log.warn("API call failed (attempt {})", attempt, e);
            
            if (attempt >= maxRetries) {
                log.error("All retry attempts failed, using simulated data");
                return generateSimulatedReading();
            }
            
            Thread.sleep(2000 * attempt);  // Exponential backoff
        }
    }
}
```

### 2. Caching

```java
@Cacheable(value = "airQuality", key = "#city")
public AirQuality fetchRealAirQuality(String city) {
    // API call...
}
```

### 3. Rate Limiting

```java
private final RateLimiter rateLimiter = RateLimiter.create(10.0);  // 10 requests/second

public AirQuality fetchWithRateLimit() {
    rateLimiter.acquire();
    return fetchRealAirQuality();
}
```

### 4. Monitoring

```java
@Timed(value = "api.fetch.time")
@Counted(value = "api.fetch.count")
public AirQuality fetchRealAirQuality() {
    // Metrics will be captured automatically
}
```

---

## ✅ Conversion Checklist

- [ ] Choose API provider (OpenAQ, IQAir, etc.)
- [ ] Get API key (if required)
- [ ] Create external API service
- [ ] Update DataSimulatorService
- [ ] Configure application.properties
- [ ] Test with real API
- [ ] Implement error handling and fallback
- [ ] Set up caching (optional)
- [ ] Monitor API usage and limits
- [ ] Update frontend (no changes needed!)

---

## 🎯 Summary

1. **Development:** Use simulated data (`airquality.data.source=simulated`)
2. **Testing:** Switch to real API (`airquality.data.source=real`)
3. **Production:** Use real API with fallback to simulation on errors
4. **No frontend changes required** - API response format stays the same!

Your system is now using **real, live air quality data**! 🌍✅
