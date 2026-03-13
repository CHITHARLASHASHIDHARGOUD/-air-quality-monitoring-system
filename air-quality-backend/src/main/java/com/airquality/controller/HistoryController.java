package com.airquality.controller;

import com.airquality.dto.RecentReadingResponse;
import com.airquality.service.AirQualityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Compatibility controller for top-level history route.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class HistoryController {

    private final AirQualityService airQualityService;

    /**
     * GET /api/history/{city}
     */
    @GetMapping("/history/{city}")
    public ResponseEntity<List<RecentReadingResponse>> getHistory(@PathVariable String city) {
        log.info("GET /api/history/{} - Fetching history", city);
        return ResponseEntity.ok(airQualityService.getHistoryByCity(city.trim()));
    }

    /**
     * GET /api/readings/{city}
     */
    @GetMapping("/readings/{city}")
    public ResponseEntity<List<RecentReadingResponse>> getReadings(@PathVariable String city) {
        log.info("GET /api/readings/{} - Fetching latest 10 readings", city);
        return ResponseEntity.ok(airQualityService.getTopReadingsByCity(city.trim()));
    }
}
