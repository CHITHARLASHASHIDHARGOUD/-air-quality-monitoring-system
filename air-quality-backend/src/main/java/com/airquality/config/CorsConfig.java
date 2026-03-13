package com.airquality.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration
 * Allows frontend to communicate with backend
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${airquality.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${airquality.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${airquality.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${airquality.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOrigins.split(","))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders(allowedHeaders.split(","))
                .allowCredentials(allowCredentials)
                .maxAge(3600);
    }
}
