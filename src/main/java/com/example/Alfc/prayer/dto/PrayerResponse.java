package com.example.Alfc.prayer.dto;

import com.example.Alfc.prayer.PrayerCategory;
import com.example.Alfc.prayer.PrayerRequest;
import com.example.Alfc.prayer.PrayerStatus;
import com.example.Alfc.prayer.PrayerVisibility;

import java.time.Instant;

/**
 * Public-safe view of a prayer request. Deliberately omits authorMemberId
 * and authorDeviceId so anonymity is preserved on the wall — both fields
 * are kept server-side for moderation, rate limiting, and "delete my own".
 *
 * The {@code mine} flag tells the calling client whether THIS request was
 * submitted by their device/account, so the UI can show delete controls
 * for their own posts without ever revealing identity to anyone else.
 */
public record PrayerResponse(
        Long id,
        String body,
        PrayerCategory category,
        PrayerVisibility visibility,
        PrayerStatus status,
        long prayCount,
        boolean prayedByMe,
        boolean mine,
        Instant createdAt
) {
    public static PrayerResponse from(PrayerRequest p, boolean prayedByMe, boolean mine) {
        return new PrayerResponse(
                p.getId(),
                p.getBody(),
                p.getCategory(),
                p.getVisibility(),
                p.getStatus(),
                p.getPrayCount(),
                prayedByMe,
                mine,
                p.getCreatedAt()
        );
    }
}
