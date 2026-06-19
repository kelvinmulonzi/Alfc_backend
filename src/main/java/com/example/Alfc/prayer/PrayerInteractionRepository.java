package com.example.Alfc.prayer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PrayerInteractionRepository extends JpaRepository<PrayerInteraction, Long> {

    boolean existsByPrayerIdAndDeviceId(Long prayerId, String deviceId);
}
