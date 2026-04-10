package com.example.Alfc.sermons;

import com.example.Alfc.sermons.dto.SermonRequest;
import com.example.Alfc.sermons.dto.SermonResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class SermonController {

    private final SermonService service;

    public SermonController(SermonService service) {
        this.service = service;
    }

    // ---- Public ----

    @GetMapping("/api/sermons")
    public List<SermonResponse> list(@RequestParam(required = false) String series) {
        var results = (series == null || series.isBlank())
                ? service.listAll()
                : service.listBySeries(series);
        return results.stream().map(SermonResponse::from).toList();
    }

    @GetMapping("/api/sermons/{id}")
    public SermonResponse get(@PathVariable Long id) {
        return SermonResponse.from(service.get(id));
    }

    // ---- Admin ----

    @PostMapping("/api/admin/sermons")
    public ResponseEntity<SermonResponse> create(@Valid @RequestBody SermonRequest req) {
        Sermon s = service.create(req);
        return ResponseEntity
                .created(URI.create("/api/sermons/" + s.getId()))
                .body(SermonResponse.from(s));
    }

    @PutMapping("/api/admin/sermons/{id}")
    public SermonResponse update(@PathVariable Long id, @Valid @RequestBody SermonRequest req) {
        return SermonResponse.from(service.update(id, req));
    }

    @DeleteMapping("/api/admin/sermons/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
