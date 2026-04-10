package com.example.Alfc.events;

import com.example.Alfc.events.dto.EventRequest;
import com.example.Alfc.events.dto.EventResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    // ---- Public ----

    @GetMapping("/api/events")
    public List<EventResponse> upcoming() {
        return service.listUpcoming().stream().map(EventResponse::from).toList();
    }

    @GetMapping("/api/events/{id}")
    public EventResponse get(@PathVariable Long id) {
        return EventResponse.from(service.get(id));
    }

    // ---- Admin ----

    @GetMapping("/api/admin/events")
    public List<EventResponse> listAll() {
        return service.listAll().stream().map(EventResponse::from).toList();
    }

    @PostMapping("/api/admin/events")
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest req) {
        Event created = service.create(req);
        return ResponseEntity
                .created(URI.create("/api/events/" + created.getId()))
                .body(EventResponse.from(created));
    }

    @PutMapping("/api/admin/events/{id}")
    public EventResponse update(@PathVariable Long id, @Valid @RequestBody EventRequest req) {
        return EventResponse.from(service.update(id, req));
    }

    @DeleteMapping("/api/admin/events/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
