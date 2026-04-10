package com.example.Alfc.devotionals.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DevotionalRequest(
        @NotNull LocalDate date,
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 200) String verseReference,
        @NotBlank @Size(max = 1000) String verseText,
        @NotBlank @Size(max = 5000) String body,
        @Size(max = 100) String author
) {}
