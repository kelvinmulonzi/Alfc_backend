package com.example.Alfc.events;

import com.example.Alfc.common.NotFoundException;
import com.example.Alfc.events.dto.EventRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class EventService {

    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Event> listUpcoming() {
        return repository.findAllByPublishedTrueAndStartsAtAfterOrderByStartsAtAsc(Instant.now());
    }

    @Transactional(readOnly = true)
    public List<Event> listAll() {
        return repository.findAllByOrderByStartsAtAsc();
    }

    @Transactional(readOnly = true)
    public Event get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event " + id + " not found"));
    }

    @Transactional
    public Event create(EventRequest req) {
        Event event = Event.builder()
                .title(req.title())
                .description(req.description())
                .startsAt(req.startsAt())
                .endsAt(req.endsAt())
                .location(req.location())
                .imageUrl(req.imageUrl())
                .category(req.category())
                .published(req.published() == null || req.published())
                .build();
        return repository.save(event);
    }

    @Transactional
    public Event update(Long id, EventRequest req) {
        Event event = get(id);
        event.setTitle(req.title());
        event.setDescription(req.description());
        event.setStartsAt(req.startsAt());
        event.setEndsAt(req.endsAt());
        event.setLocation(req.location());
        event.setImageUrl(req.imageUrl());
        event.setCategory(req.category());
        if (req.published() != null) event.setPublished(req.published());
        return event;
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Event " + id + " not found");
        }
        repository.deleteById(id);
    }
}
