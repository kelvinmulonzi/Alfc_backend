package com.example.Alfc.prayer;

import com.example.Alfc.prayer.dto.PrayerAdminResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminPrayerController {

    private final PrayerService service;

    public AdminPrayerController(PrayerService service) {
        this.service = service;
    }

    @GetMapping("/api/admin/prayers")
    public List<PrayerAdminResponse> list(
            @RequestParam(required = false) PrayerStatus status,
            @RequestParam(required = false) PrayerVisibility visibility,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Page<PrayerRequest> result = service.adminList(status, visibility, page, size);
        return result.getContent().stream().map(PrayerAdminResponse::from).toList();
    }

    @PostMapping("/api/admin/prayers/{id}/hide")
    public PrayerAdminResponse hide(@PathVariable Long id) {
        return PrayerAdminResponse.from(service.adminSetStatus(id, PrayerStatus.HIDDEN));
    }

    @PostMapping("/api/admin/prayers/{id}/restore")
    public PrayerAdminResponse restore(@PathVariable Long id) {
        return PrayerAdminResponse.from(service.adminSetStatus(id, PrayerStatus.ACTIVE));
    }

    @PostMapping("/api/admin/prayers/{id}/archive")
    public PrayerAdminResponse archive(@PathVariable Long id) {
        return PrayerAdminResponse.from(service.adminSetStatus(id, PrayerStatus.ARCHIVED));
    }
}
