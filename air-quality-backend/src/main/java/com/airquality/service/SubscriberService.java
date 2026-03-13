package com.airquality.service;

import com.airquality.dto.SubscriptionRequest;
import com.airquality.dto.SubscriptionResponse;
import com.airquality.entity.Subscriber;
import com.airquality.exception.DuplicateResourceException;
import com.airquality.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for Subscriber management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    /**
     * Create new subscription
     */
    @Transactional
    public SubscriptionResponse subscribe(SubscriptionRequest request) {
        log.info("Processing subscription request for email: {}", request.getEmail());

        // Check if email already exists
        if (subscriberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                "Email " + request.getEmail() + " is already subscribed"
            );
        }

        // Create new subscriber
        Subscriber subscriber = Subscriber.builder()
                .name(request.getName())
                .email(request.getEmail())
                .threshold(request.getThreshold())
            .city(request.getCity())
                .isActive(true)
                .build();

        Subscriber saved = subscriberRepository.save(subscriber);
        
        log.info("Subscription created successfully for {}", saved.getEmail());

        return SubscriptionResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .threshold(saved.getThreshold())
                .city(saved.getCity())
                .message("Successfully subscribed to air quality alerts!")
                .build();
    }

    /**
     * Get all active subscribers
     */
    public List<Subscriber> getAllActiveSubscribers() {
        return subscriberRepository.findByIsActiveTrue();
    }

    /**
     * Get subscribers who should be alerted for given AQI
     */
    public List<Subscriber> getSubscribersForAlert(int currentAqi) {
        return subscriberRepository.findByIsActiveTrueAndThresholdLessThanEqual(currentAqi);
    }

    /**
     * Get city subscribers who should be alerted for given AQI
     */
    public List<Subscriber> getSubscribersForAlert(String city, int currentAqi) {
        return subscriberRepository.findByIsActiveTrueAndCityIgnoreCaseAndThresholdLessThanEqual(city, currentAqi);
    }

    /**
     * Unsubscribe (soft delete - set inactive)
     */
    @Transactional
    public void unsubscribe(String email) {
        Subscriber subscriber = subscriberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Subscriber not found"));
        
        subscriber.setIsActive(false);
        subscriberRepository.save(subscriber);
        
        log.info("Subscriber {} unsubscribed", email);
    }
}
