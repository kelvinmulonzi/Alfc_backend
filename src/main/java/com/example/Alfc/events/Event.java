package com.example.Alfc.events;

import com.example.Alfc.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class
Event extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Instant startsAt;

    private Instant endsAt;

    @Column(length = 200)
    private String location;

    @Column(length = 500)
    private String imageUrl;

    /** Free-form category like "Service", "Youth", "Bible Study". */
    @Column(length = 50)
    private String category;

    @Column(nullable = false)
    private boolean published = true;
}
