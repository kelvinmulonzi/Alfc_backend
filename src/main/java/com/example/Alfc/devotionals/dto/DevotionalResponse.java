package com.example.Alfc.devotionals.dto;

import com.example.Alfc.devotionals.Devotional;

import java.time.LocalDate;

public record DevotionalResponse(
        Long id,
        LocalDate date,
        String title,
        String verseReference,
        String verseText,
        String body,
        String author
) {
    public static DevotionalResponse from(Devotional d) {
        return new DevotionalResponse(
                d.getId(),
                d.getDate(),
                d.getTitle(),
                d.getVerseReference(),
                d.getVerseText(),
                d.getBody(),
                d.getAuthor()
        );
    }
}
