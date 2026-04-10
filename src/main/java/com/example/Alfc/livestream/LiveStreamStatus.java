package com.example.Alfc.livestream;

import java.time.Instant;

public record LiveStreamStatus(
        boolean live,
        String videoId,
        String title,
        String thumbnailUrl,
        Instant startedAt,
        Instant checkedAt
) {
    public static LiveStreamStatus offline(Instant checkedAt) {
        return new LiveStreamStatus(false, null, null, null, null, checkedAt);
    }
}
