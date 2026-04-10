package com.example.Alfc.events.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record EventRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 2000) String description,
        @NotNull Instant startsAt,
        Instant endsAt,
        @Size(max = 200) String location,
        @Size(max = 500) String imageUrl,
        @Size(max = 50) String category,
        Boolean published
) {}
