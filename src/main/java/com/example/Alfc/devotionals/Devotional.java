package com.example.Alfc.devotionals;

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
@Table(name = "devotionals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Devotional extends BaseEntity {

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 200)
    private String verseReference;

    @Column(nullable = false, length = 1000)
    private String verseText;

    @Column(nullable = false, length = 5000)
    private String body;

    @Column(length = 100)
    private String author;
}
