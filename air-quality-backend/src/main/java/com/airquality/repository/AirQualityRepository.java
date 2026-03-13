package com.airquality.repository;

import com.airquality.entity.AirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Air Quality data access
 */
@Repository
public interface AirQualityRepository extends JpaRepository<AirQuality, Long> {

    /**
     * Find the most recent air quality reading
     */
    Optional<AirQuality> findFirstByOrderByRecordedAtDesc();

    /**
     * Find recent readings (last N records)
     */
    List<AirQuality> findTop12ByOrderByRecordedAtDesc();

    /**
     * Find recent readings for a specific location (last 12 records)
     */
    List<AirQuality> findTop12ByLocationIgnoreCaseOrderByRecordedAtDesc(String location);

    /**
     * Find full history for a specific location/city
     */
    List<AirQuality> findByLocationIgnoreCaseOrderByRecordedAtDesc(String location);

    /**
     * Find top 10 readings for a specific city/location
     */
    List<AirQuality> findTop10ByLocationIgnoreCaseOrderByRecordedAtDesc(String location);

    /**
     * Compatibility method: city maps to location
     */
    default List<AirQuality> findByCityOrderByRecordedAtDesc(String city) {
        return findByLocationIgnoreCaseOrderByRecordedAtDesc(city);
    }

    /**
     * Compatibility method: city maps to location
     */
    default List<AirQuality> findTop10ByCityOrderByRecordedAtDesc(String city) {
        return findTop10ByLocationIgnoreCaseOrderByRecordedAtDesc(city);
    }

    /**
     * Find readings within a time range
     */
    List<AirQuality> findByRecordedAtBetweenOrderByRecordedAtDesc(
        LocalDateTime start, 
        LocalDateTime end
    );

    /**
     * Count total readings
     */
    long count();

    /**
     * Delete oldest records beyond limit
     */
    @Modifying
    @Query("DELETE FROM AirQuality a WHERE a.id IN " +
           "(SELECT a2.id FROM AirQuality a2 ORDER BY a2.recordedAt ASC " +
           "LIMIT :limit OFFSET :maxRecords)")
    void deleteOldestRecords(int limit, int maxRecords);

    /**
     * Delete records older than specified date
     */
    @Modifying
    @Query("DELETE FROM AirQuality a WHERE a.recordedAt < :cutoffDate")
    void deleteRecordsOlderThan(LocalDateTime cutoffDate);
}
