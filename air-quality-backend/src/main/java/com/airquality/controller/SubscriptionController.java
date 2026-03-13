package com.airquality.controller;

import com.airquality.dto.ApiResponse;
import com.airquality.dto.SubscriptionRequest;
import com.airquality.dto.SubscriptionResponse;
import com.airquality.service.AlertService;
import com.airquality.service.SubscriberService;
import com.airquality.entity.Subscriber;
import com.airquality.repository.SubscriberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Subscription endpoints
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SubscriptionController {

    private final SubscriberService subscriberService;
    private final AlertService alertService;
    private final SubscriberRepository subscriberRepository;

    /**
     * POST /api/subscribe
     * Subscribe a citizen to air quality alerts
     * 
     * Request body:
     * {
     *   "name": "John Doe",
     *   "email": "john@gmail.com",
     *   "threshold": 150
     * }
     * 
     * Response:
     * {
     *   "success": true,
     *   "message": "Successfully subscribed to air quality alerts!",
     *   "data": {
     *     "id": 1,
     *     "name": "John Doe",
     *     "email": "john@gmail.com",
     *     "threshold": 150,
     *     "message": "Successfully subscribed to air quality alerts!"
     *   },
     *   "timestamp": 1234567890
     * }
     */
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> subscribe(
            @Valid @RequestBody SubscriptionRequest request) {
        
        log.info("POST /api/subscribe - New subscription request for {}", request.getEmail());
        
        SubscriptionResponse response = subscriberService.subscribe(request);

        // Immediately check if current AQI for the city exceeds threshold and send email
        subscriberRepository.findByEmail(request.getEmail()).ifPresent(subscriber -> {
            boolean alertSent = alertService.checkAndAlertSubscriber(subscriber);
            if (alertSent) {
                response.setMessage("Subscribed! AQI alert email sent — current AQI exceeds your threshold.");
            }
        });

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subscription created successfully", response));
    }

    /**
     * POST /api/check-alert
     * Manually trigger an alert check for an existing subscriber.
     * If current AQI for their city >= their threshold, an email is sent.
     */
    @PostMapping("/check-alert")
    public ResponseEntity<ApiResponse<String>> checkAlert(@RequestBody java.util.Map<String, String> body) {
        String email = body.get("email");
        log.info("POST /api/check-alert - Check alert for {}", email);

        Subscriber subscriber = subscriberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Subscriber not found for email: " + email));

        boolean sent = alertService.checkAndAlertSubscriber(subscriber);
        String msg = sent
                ? "Alert email sent! AQI exceeds your threshold of " + subscriber.getThreshold()
                : "AQI is currently below your threshold of " + subscriber.getThreshold() + ". No alert needed.";

        return ResponseEntity.ok(ApiResponse.success(msg));
    }

    /**
     * DELETE /api/unsubscribe/{email}
     * Unsubscribe from alerts (optional feature)
     */
    @DeleteMapping("/unsubscribe/{email}")
    public ResponseEntity<ApiResponse<String>> unsubscribe(@PathVariable String email) {
        log.info("DELETE /api/unsubscribe/{} - Unsubscribe request", email);
        
        subscriberService.unsubscribe(email);
        
        return ResponseEntity.ok(
                ApiResponse.success("Successfully unsubscribed from alerts", email)
        );
    }
}
