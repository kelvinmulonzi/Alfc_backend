package com.example.Alfc.devotionals;

import com.example.Alfc.devotionals.dto.DevotionalRequest;
import com.example.Alfc.devotionals.dto.DevotionalResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class DevotionalController {

    private final DevotionalService service;

    public DevotionalController(DevotionalService service) {
        this.service = service;
    }

    // ---- Public ----

    @GetMapping("/api/devotionals/today")
    public DevotionalResponse today() {
        return DevotionalResponse.from(service.today());
    }

    @GetMapping("/api/devotionals")
    public List<DevotionalResponse> list() {
        return service.listAll().stream().map(DevotionalResponse::from).toList();
    }

    @GetMapping("/api/devotionals/{id}")
    public DevotionalResponse get(@PathVariable Long id) {
        return DevotionalResponse.from(service.get(id));
    }

    // ---- Admin ----

    @PostMapping("/api/admin/devotionals")
    public ResponseEntity<DevotionalResponse> create(@Valid @RequestBody DevotionalRequest req) {
        Devotional d = service.create(req);
        return ResponseEntity
                .created(URI.create("/api/devotionals/" + d.getId()))
                .body(DevotionalResponse.from(d));
    }

    @PutMapping("/api/admin/devotionals/{id}")
    public DevotionalResponse update(@PathVariable Long id, @Valid @RequestBody DevotionalRequest req) {
        return DevotionalResponse.from(service.update(id, req));
    }

    @DeleteMapping("/api/admin/devotionals/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
