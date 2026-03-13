package com.airquality.repository;

import com.airquality.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Subscriber data access
 */
@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

    /**
     * Check if email already exists
     */
    boolean existsByEmail(String email);

    /**
     * Find subscriber by email
     */
    Optional<Subscriber> findByEmail(String email);

    /**
     * Find all active subscribers
     */
    List<Subscriber> findByIsActiveTrue();

    /**
     * Find subscribers by city
     */
    List<Subscriber> findByCityIgnoreCase(String city);

    /**
     * Compatibility method name
     */
    default List<Subscriber> findByCity(String city) {
        return findByCityIgnoreCase(city);
    }

    /**
     * Find active subscribers with threshold below or equal to given AQI
     */
    List<Subscriber> findByIsActiveTrueAndThresholdLessThanEqual(Integer aqi);

    /**
     * Find active city subscribers with threshold below or equal to given AQI
     */
    List<Subscriber> findByIsActiveTrueAndCityIgnoreCaseAndThresholdLessThanEqual(String city, Integer aqi);
}
