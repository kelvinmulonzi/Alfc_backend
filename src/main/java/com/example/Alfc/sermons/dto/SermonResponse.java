package com.example.Alfc.sermons.dto;

import com.example.Alfc.sermons.Sermon;

import java.time.LocalDate;

public record SermonResponse(
        Long id,
        String title,
        String speaker,
        String scripture,
        String summary,
        LocalDate preachedOn,
        String youtubeVideoId,
        String audioUrl,
        String thumbnailUrl,
        String series
) {
    public static SermonResponse from(Sermon s) {
        return new SermonResponse(
                s.getId(),
                s.getTitle(),
                s.getSpeaker(),
                s.getScripture(),
                s.getSummary(),
                s.getPreachedOn(),
                s.getYoutubeVideoId(),
                s.getAudioUrl(),
                s.getThumbnailUrl(),
                s.getSeries()
        );
    }
}
