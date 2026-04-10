package com.example.Alfc.events;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByPublishedTrueAndStartsAtAfterOrderByStartsAtAsc(Instant after);

    List<Event> findAllByOrderByStartsAtAsc();
}
