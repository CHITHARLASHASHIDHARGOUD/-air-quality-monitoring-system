package com.airquality.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient Configuration
 * For future integration with external air quality APIs
 */
@Configuration
public class WebClientConfig {

    /**
     * WebClient for external API calls
     * Can be customized with timeout, headers, etc.
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build();
    }

    /**
     * Example: WebClient configured for specific API
     * Uncomment and configure when integrating with real AQI API
     */
    /*
    @Bean
    public WebClient aqiApiWebClient(@Value("${airquality.api.url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .build();
    }
    */
}
