package com.example.Alfc.events.dto;

import com.example.Alfc.events.Event;

import java.time.Instant;

public record EventResponse(
        Long id,
        String title,
        String description,
        Instant startsAt,
        Instant endsAt,
        String location,
        String imageUrl,
        String category,
        boolean published,
        Instant createdAt,
        Instant updatedAt
) {
    public static EventResponse from(Event e) {
        return new EventResponse(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getStartsAt(),
                e.getEndsAt(),
                e.getLocation(),
                e.getImageUrl(),
                e.getCategory(),
                e.isPublished(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
