package com.example.Alfc.devotionals;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DevotionalRepository extends JpaRepository<Devotional, Long> {

    Optional<Devotional> findByDate(LocalDate date);

    List<Devotional> findAllByOrderByDateDesc();
}
