package com.airquality.service;

import com.airquality.entity.AirQuality;
import com.airquality.entity.Subscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${airquality.email.enabled:true}")
    private boolean emailEnabled;

    public void sendAlertEmail(Subscriber subscriber, AirQuality airQuality) {
        if (!emailEnabled) {
            log.debug("Email sending is disabled");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(subscriber.getEmail());
            message.setSubject("Air Quality Alert: " + airQuality.getCategory());
            message.setText(buildEmailBody(subscriber, airQuality));

            mailSender.send(message);

            log.info("Alert email sent to {}", subscriber.getEmail());

        } catch (Exception e) {
            log.error("Failed to send email to {}", subscriber.getEmail(), e);
        }
    }

    private String buildEmailBody(Subscriber subscriber, AirQuality airQuality) {
        return String.format(
            "Dear %s,\n\n" +
            "ALERT: Air Quality Threshold Exceeded!\n\n" +
            "Location: %s\n" +
            "Current AQI: %d (%s)\n" +
            "Your Threshold: %d\n\n" +
            "Pollutant Levels:\n" +
            "  PM2.5: %.1f ug/m3\n" +
            "  PM10: %.1f ug/m3\n" +
            "  CO: %.2f ppm\n" +
            "  NO2: %.1f ppb\n" +
            "  O3: %.1f ppb\n\n" +
            "Health Advisory:\n" +
            "%s\n\n" +
            "Stay safe and minimize outdoor exposure.\n\n" +
            "---\n" +
            "Urban Air Quality Monitoring System\n",
            subscriber.getName(),
            airQuality.getLocation(),
            airQuality.getAqi(),
            airQuality.getCategory(),
            subscriber.getThreshold(),
            airQuality.getPm25(),
            airQuality.getPm10(),
            airQuality.getCo(),
            airQuality.getNo2(),
            airQuality.getO3(),
            airQuality.getHealthMessage()
        );
    }

    public void sendWelcomeEmail(Subscriber subscriber) {
        if (!emailEnabled) {
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(subscriber.getEmail());
            message.setSubject("Welcome to Air Quality Alerts");
            message.setText(String.format(
                "Dear %s,\n\n" +
                "Thank you for subscribing to Air Quality Alerts!\n\n" +
                "City: %s\n" +
                "You will receive notifications when the Air Quality Index (AQI) exceeds %d.\n\n" +
                "Stay informed and breathe easier!\n\n" +
                "---\n" +
                "Urban Air Quality Monitoring System",
                subscriber.getName(),
                subscriber.getCity(),
                subscriber.getThreshold()
            ));

            mailSender.send(message);
            log.info("Welcome email sent to {}", subscriber.getEmail());

        } catch (Exception e) {
            log.error("Failed to send welcome email to {}", subscriber.getEmail(), e);
        }
    }
}
