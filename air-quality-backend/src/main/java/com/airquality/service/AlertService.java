package com.airquality.service;

import com.airquality.entity.AirQuality;
import com.airquality.entity.AlertLog;
import com.airquality.entity.Subscriber;
import com.airquality.repository.AlertLogRepository;
import com.airquality.repository.AirQualityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for Alert management and checking
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertLogRepository alertLogRepository;
    private final AirQualityRepository airQualityRepository;
    private final SubscriberService subscriberService;
    private final EmailService emailService;

    @Value("${airquality.alert.enabled:true}")
    private boolean alertEnabled;

    /**
     * Check if alerts should be sent for current AQI
     */
    @Transactional
    public void checkAndSendAlerts(AirQuality currentReading) {
        if (!alertEnabled) {
            log.debug("Alert checking is disabled");
            return;
        }

        int currentAqi = currentReading.getAqi();
        String city = currentReading.getLocation();
        log.debug("Checking alerts for AQI: {}", currentAqi);

        // Get subscribers who should be alerted
        List<Subscriber> subscribersToAlert = subscriberService.getSubscribersForAlert(city, currentAqi);

        if (subscribersToAlert.isEmpty()) {
            log.debug("No subscribers need to be alerted for city={} AQI={}", city, currentAqi);
            return;
        }

        log.info("Found {} subscribers to alert for city={} AQI={}", subscribersToAlert.size(), city, currentAqi);

        for (Subscriber subscriber : subscribersToAlert) {
            // Check if we already sent alert recently (within last 30 minutes)
            LocalDateTime recentCutoff = LocalDateTime.now().minusMinutes(30);
            boolean recentlySent = alertLogRepository.existsBySubscriberEmailAndSentAtAfter(
                    subscriber.getEmail(), 
                    recentCutoff
            );

            if (recentlySent) {
                log.debug("Alert already sent recently to {}, skipping", subscriber.getEmail());
                continue;
            }

            // Send email alert
            emailService.sendAlertEmail(subscriber, currentReading);

            // Create alert log
            AlertLog alertLog = AlertLog.builder()
                    .subscriberEmail(subscriber.getEmail())
                    .subscriberName(subscriber.getName())
                    .threshold(subscriber.getThreshold())
                    .currentAqi(currentAqi)
                    .category(currentReading.getCategory())
                    .healthMessage(currentReading.getHealthMessage())
                    .emailSent(true)
                    .build();

            alertLogRepository.save(alertLog);

            log.info("Alert emailed and logged for {} - AQI {} exceeds threshold {}", 
                     subscriber.getEmail(), currentAqi, subscriber.getThreshold());
        }
    }

    /**
     * Get recent alert logs (for monitoring)
     */
    public List<AlertLog> getRecentAlerts() {
        return alertLogRepository.findTop50ByOrderBySentAtDesc();
    }

    /**
     * Get alerts for specific subscriber
     */
    public List<AlertLog> getAlertsForSubscriber(String email) {
        return alertLogRepository.findBySubscriberEmailOrderBySentAtDesc(email);
    }

    /**
     * Immediately check if a subscriber should receive an alert
     * based on the latest AQI reading for their city.
     * Called right after subscription so the user gets an email
     * if the current AQI already exceeds their threshold.
     */
    @Transactional
    public boolean checkAndAlertSubscriber(Subscriber subscriber) {
        String city = subscriber.getCity();
        int threshold = subscriber.getThreshold();

        Optional<AirQuality> latestOpt = airQualityRepository
                .findTop12ByLocationIgnoreCaseOrderByRecordedAtDesc(city)
                .stream().findFirst();

        if (latestOpt.isEmpty()) {
            log.info("No AQI data for city={}, skipping immediate alert for {}", city, subscriber.getEmail());
            return false;
        }

        AirQuality latest = latestOpt.get();
        int currentAqi = latest.getAqi();

        if (currentAqi < threshold) {
            log.info("AQI {} for city={} is below threshold {} for {}", currentAqi, city, threshold, subscriber.getEmail());
            return false;
        }

        // Send email
        emailService.sendAlertEmail(subscriber, latest);

        // Log the alert
        AlertLog alertLog = AlertLog.builder()
                .subscriberEmail(subscriber.getEmail())
                .subscriberName(subscriber.getName())
                .threshold(threshold)
                .currentAqi(currentAqi)
                .category(latest.getCategory())
                .healthMessage(latest.getHealthMessage())
                .emailSent(true)
                .build();
        alertLogRepository.save(alertLog);

        log.info("Immediate alert sent to {} - city={} AQI {} >= threshold {}",
                 subscriber.getEmail(), city, currentAqi, threshold);
        return true;
    }
}
