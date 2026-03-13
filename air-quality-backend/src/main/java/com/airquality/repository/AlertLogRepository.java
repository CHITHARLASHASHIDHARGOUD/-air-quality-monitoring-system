package com.airquality.repository;

import com.airquality.entity.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Alert Log data access
 */
@Repository
public interface AlertLogRepository extends JpaRepository<AlertLog, Long> {

    /**
     * Find alerts for a specific subscriber
     */
    List<AlertLog> findBySubscriberEmailOrderBySentAtDesc(String email);

    /**
     * Find recent alerts
     */
    List<AlertLog> findTop50ByOrderBySentAtDesc();

    /**
     * Find alerts sent within time range
     */
    List<AlertLog> findBySentAtBetweenOrderBySentAtDesc(
        LocalDateTime start, 
        LocalDateTime end
    );

    /**
     * Check if alert was recently sent to subscriber
     * (to avoid duplicate alerts within short time)
     */
    boolean existsBySubscriberEmailAndSentAtAfter(
        String email, 
        LocalDateTime cutoffTime
    );
}
