package com.airquality;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Application Entry Point
 * Urban Air Quality Monitoring & Citizen Alert System
 * 
 * @author Backend Architect
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class AirQualityApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirQualityApplication.class, args);
        System.out.println("""
                
                ═══════════════════════════════════════════════════════════════
                  Urban Air Quality Monitoring System - Backend Started
                  API Base URL: http://localhost:8080/api
                  Health Check: http://localhost:8080/actuator/health
                ═══════════════════════════════════════════════════════════════
                """);
    }
}
