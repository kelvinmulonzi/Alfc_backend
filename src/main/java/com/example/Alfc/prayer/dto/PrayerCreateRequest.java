package com.example.Alfc.prayer.dto;

import com.example.Alfc.prayer.PrayerCategory;
import com.example.Alfc.prayer.PrayerVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PrayerCreateRequest(
        @NotBlank @Size(max = 500) String body,
        @NotNull PrayerCategory category,
        @NotNull PrayerVisibility visibility
) {}
