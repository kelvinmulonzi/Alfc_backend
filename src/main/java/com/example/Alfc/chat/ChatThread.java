package com.example.Alfc.chat;

import com.example.Alfc.auth.Member;
import com.example.Alfc.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * A 1:1 conversation between two members. Participants are stored in
 * normalized order: participantA.id < participantB.id, so lookups are
 * deterministic and uniqueness is enforced.
 */
@Entity
@Table(
        name = "chat_threads",
        uniqueConstraints = @UniqueConstraint(columnNames = {"participant_a_id", "participant_b_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class
ChatThread extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_a_id")
    private Member participantA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_b_id")
    private Member participantB;

    @Column
    private Instant lastMessageAt;

    @Column(length = 500)
    private String lastMessagePreview;

    @Column
    private Instant participantALastReadAt;

    @Column
    private Instant participantBLastReadAt;
}
