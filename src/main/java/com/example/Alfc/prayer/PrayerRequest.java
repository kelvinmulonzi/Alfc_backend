package com.example.Alfc.prayer;

import com.example.Alfc.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prayer_requests", indexes = {
        @Index(name = "idx_prayer_status_visibility", columnList = "status,visibility"),
        @Index(name = "idx_prayer_author_device", columnList = "author_device_id"),
        @Index(name = "idx_prayer_author_member", columnList = "author_member_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrayerRequest extends BaseEntity {

    @Column(nullable = false, length = 500)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrayerCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrayerVisibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrayerStatus status;

    @Column(nullable = false)
    @Builder.Default
    private long prayCount = 0L;

    /** Set when the submitter was logged in. Never exposed in public payloads. */
    @Column
    private Long authorMemberId;

    /** Stable device id sent by the app. Never exposed in public payloads. */
    @Column(length = 64)
    private String authorDeviceId;
}
