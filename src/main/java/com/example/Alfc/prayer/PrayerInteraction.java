package com.example.Alfc.prayer;

import com.example.Alfc.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Records every tap of "I prayed for you" — each tap is a separate row.
 * Same device can pray for the same request many times (people pray more
 * than once), so there is no uniqueness constraint. The (prayer_id,
 * device_id) lookup is still cheap via the index, and we use it to set
 * {@code prayedByMe} on read so the heart fills in.
 */
@Entity
@Table(
        name = "prayer_interactions",
        indexes = @Index(name = "idx_prayer_interactions_prayer", columnList = "prayer_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrayerInteraction extends BaseEntity {

    @Column(nullable = false)
    private Long prayerId;

    @Column(nullable = false, length = 64)
    private String deviceId;
}
