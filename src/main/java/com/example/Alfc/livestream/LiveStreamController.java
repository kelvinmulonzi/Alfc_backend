package com.example.Alfc.livestream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LiveStreamController {

    private final LiveStreamService service;

    public LiveStreamController(LiveStreamService service) {
        this.service = service;
    }

    @GetMapping("/api/live/status")
    public LiveStreamStatus status() {
        return service.current();
    }

    /** Admin-only: force a refresh from YouTube without waiting for the next poll. */
    @PostMapping("/api/admin/live/refresh")
    public LiveStreamStatus refresh() {
        service.refresh();
        return service.current();
    }
}
