package com.airquality.controller;

import com.airquality.dto.ApiResponse;
import com.airquality.service.DataSimulatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for testing and simulation control
 * For development/testing purposes only
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TestController {

    private final DataSimulatorService dataSimulatorService;

    /**
     * POST /api/test/pollution-spike
     * Generate a pollution spike for testing alerts
     */
    @PostMapping("/pollution-spike")
    public ResponseEntity<ApiResponse<String>> generatePollutionSpike() {
        log.info("POST /api/test/pollution-spike - Generating test pollution spike");
        
        dataSimulatorService.generatePollutionSpike();
        
        return ResponseEntity.ok(
                ApiResponse.success("Pollution spike generated. Next reading will show high AQI.")
        );
    }

    /**
     * POST /api/test/reset-quality
     * Reset to good air quality
     */
    @PostMapping("/reset-quality")
    public ResponseEntity<ApiResponse<String>> resetAirQuality() {
        log.info("POST /api/test/reset-quality - Resetting to good air quality");
        
        dataSimulatorService.resetToGoodQuality();
        
        return ResponseEntity.ok(
                ApiResponse.success("Air quality reset to good levels.")
        );
    }

    /**
     * POST /api/test/generate-now
     * Force immediate data generation (instead of waiting for scheduled task)
     */
    @PostMapping("/generate-now")
    public ResponseEntity<ApiResponse<String>> generateDataNow() {
        log.info("POST /api/test/generate-now - Forcing immediate data generation");
        
        dataSimulatorService.generateSimulatedData();
        
        return ResponseEntity.ok(
                ApiResponse.success("New air quality data generated immediately.")
        );
    }
}
