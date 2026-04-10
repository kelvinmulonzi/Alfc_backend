package com.example.Alfc.sermons.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SermonRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 100) String speaker,
        @Size(max = 200) String scripture,
        @Size(max = 2000) String summary,
        @NotNull LocalDate preachedOn,
        @Size(max = 50) String youtubeVideoId,
        @Size(max = 500) String audioUrl,
        @Size(max = 500) String thumbnailUrl,
        @Size(max = 50) String series
) {}
