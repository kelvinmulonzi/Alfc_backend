package com.example.Alfc.prayer.dto;

import com.example.Alfc.prayer.PrayerCategory;
import com.example.Alfc.prayer.PrayerRequest;
import com.example.Alfc.prayer.PrayerStatus;
import com.example.Alfc.prayer.PrayerVisibility;

import java.time.Instant;

/**
 * Admin-only view that DOES include the author hints so the prayer team
 * can follow up on private requests or block abusers. Never returned by
 * the public endpoints.
 */
public record PrayerAdminResponse(
        Long id,
        String body,
        PrayerCategory category,
        PrayerVisibility visibility,
        PrayerStatus status,
        long prayCount,
        Long authorMemberId,
        String authorDeviceId,
        Instant createdAt
) {
    public static PrayerAdminResponse from(PrayerRequest p) {
        return new PrayerAdminResponse(
                p.getId(),
                p.getBody(),
                p.getCategory(),
                p.getVisibility(),
                p.getStatus(),
                p.getPrayCount(),
                p.getAuthorMemberId(),
                p.getAuthorDeviceId(),
                p.getCreatedAt()
        );
    }
}
