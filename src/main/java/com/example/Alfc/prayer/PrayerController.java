package com.example.Alfc.prayer;

import com.example.Alfc.auth.MemberPrincipal;
import com.example.Alfc.prayer.dto.PrayCountResponse;
import com.example.Alfc.prayer.dto.PrayerCreateRequest;
import com.example.Alfc.prayer.dto.PrayerResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class PrayerController {

    public static final String DEVICE_HEADER = "X-Device-Id";

    private final PrayerService service;

    public PrayerController(PrayerService service) {
        this.service = service;
    }

    @PostMapping("/api/prayers")
    public ResponseEntity<PrayerResponse> create(
            @Valid @RequestBody PrayerCreateRequest req,
            @RequestHeader(value = DEVICE_HEADER, required = false) String deviceId
    ) {
        Long memberId = currentMemberIdOrNull();
        PrayerRequest p = service.create(req, memberId, deviceId);
        // Public visibility ones: respond with full payload.
        // Prayer-team-only ones: return the row so the submitter sees confirmation,
        // but they won't appear on the wall.
        PrayerResponse body = PrayerResponse.from(p, false, true);
        return ResponseEntity.created(URI.create("/api/prayers/" + p.getId())).body(body);
    }

    @GetMapping("/api/prayers")
    public List<PrayerResponse> wall(
            @RequestParam(required = false) PrayerCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = DEVICE_HEADER, required = false) String deviceId
    ) {
        Long memberId = currentMemberIdOrNull();
        Page<PrayerRequest> result = service.publicWall(category, page, size);
        return result.getContent().stream()
                .map(p -> PrayerResponse.from(
                        p,
                        service.prayedByMe(p.getId(), deviceId),
                        isMine(p, memberId, deviceId)))
                .toList();
    }

    @PostMapping("/api/prayers/{id}/pray")
    public PrayCountResponse pray(
            @PathVariable Long id,
            @RequestHeader(value = DEVICE_HEADER, required = false) String deviceId
    ) {
        long count = service.pray(id, deviceId);
        return new PrayCountResponse(count, true);
    }

    @DeleteMapping("/api/prayers/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = DEVICE_HEADER, required = false) String deviceId
    ) {
        service.deleteOwn(id, currentMemberIdOrNull(), deviceId);
        return ResponseEntity.noContent().build();
    }

    private static Long currentMemberIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof MemberPrincipal mp) {
            return mp.memberId();
        }
        return null;
    }

    private static boolean isMine(PrayerRequest p, Long memberId, String deviceId) {
        if (memberId != null && memberId.equals(p.getAuthorMemberId())) return true;
        return deviceId != null && !deviceId.isBlank()
                && deviceId.equals(p.getAuthorDeviceId());
    }
}
