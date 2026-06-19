package com.example.Alfc.prayer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface PrayerRequestRepository extends JpaRepository<PrayerRequest, Long> {

    Page<PrayerRequest> findByStatusAndVisibilityOrderByCreatedAtDesc(
            PrayerStatus status, PrayerVisibility visibility, Pageable pageable);

    Page<PrayerRequest> findByStatusAndVisibilityAndCategoryOrderByCreatedAtDesc(
            PrayerStatus status, PrayerVisibility visibility, PrayerCategory category, Pageable pageable);

    Page<PrayerRequest> findByVisibilityOrderByCreatedAtDesc(
            PrayerVisibility visibility, Pageable pageable);

    Page<PrayerRequest> findByStatusOrderByCreatedAtDesc(
            PrayerStatus status, Pageable pageable);

    long countByAuthorDeviceIdAndCreatedAtAfter(String deviceId, Instant after);
}
