package com.example.Alfc.sermons;

import com.example.Alfc.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "sermons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sermon extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 100)
    private String speaker;

    @Column(length = 200)
    private String scripture;

    @Column(length = 2000)
    private String summary;

    @Column(nullable = false)
    private LocalDate preachedOn;

    /** YouTube video id, if archived on YouTube. */
    @Column(length = 50)
    private String youtubeVideoId;

    /** Optional direct audio URL (mp3) for the in-app audio player. */
    @Column(length = 500)
    private String audioUrl;

    @Column(length = 500)
    private String thumbnailUrl;

    @Column(length = 50)
    private String series;
}
