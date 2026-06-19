package com.example.Alfc.prayer;

import com.example.Alfc.common.NotFoundException;
import com.example.Alfc.prayer.dto.PrayerCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class PrayerService {

    /** A device may not submit more than this many requests per rolling hour. */
    private static final int RATE_LIMIT_PER_HOUR = 5;

    private final PrayerRequestRepository requests;
    private final PrayerInteractionRepository interactions;
    private final ProfanityFilter profanity;

    public PrayerService(PrayerRequestRepository requests,
                         PrayerInteractionRepository interactions,
                         ProfanityFilter profanity) {
        this.requests = requests;
        this.interactions = interactions;
        this.profanity = profanity;
    }

    @Transactional
    public PrayerRequest create(PrayerCreateRequest req, Long memberId, String deviceId) {
        if (!profanity.isClean(req.body())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Please reword your request — the wall is shared with the whole church.");
        }
        if (deviceId != null && !deviceId.isBlank()) {
            Instant since = Instant.now().minus(1, ChronoUnit.HOURS);
            long recent = requests.countByAuthorDeviceIdAndCreatedAtAfter(deviceId, since);
            if (recent >= RATE_LIMIT_PER_HOUR) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                        "You've submitted several requests recently. Please try again later.");
            }
        }
        return requests.save(PrayerRequest.builder()
                .body(req.body().trim())
                .category(req.category())
                .visibility(req.visibility())
                .status(PrayerStatus.ACTIVE)
                .prayCount(0L)
                .authorMemberId(memberId)
                .authorDeviceId(deviceId)
                .build());
    }

    @Transactional(readOnly = true)
    public Page<PrayerRequest> publicWall(PrayerCategory category, int page, int size) {
        Pageable pg = PageRequest.of(Math.max(0, page), Math.min(50, Math.max(1, size)));
        if (category == null) {
            return requests.findByStatusAndVisibilityOrderByCreatedAtDesc(
                    PrayerStatus.ACTIVE, PrayerVisibility.PUBLIC_WALL, pg);
        }
        return requests.findByStatusAndVisibilityAndCategoryOrderByCreatedAtDesc(
                PrayerStatus.ACTIVE, PrayerVisibility.PUBLIC_WALL, category, pg);
    }

    @Transactional(readOnly = true)
    public PrayerRequest get(Long id) {
        return requests.findById(id)
                .orElseThrow(() -> new NotFoundException("Prayer request " + id + " not found"));
    }

    /** Each call records a fresh prayer and increments the count — same device
     *  can pray for the same request many times. */
    @Transactional
    public long pray(Long prayerId, String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing device id");
        }
        PrayerRequest p = get(prayerId);
        if (p.getStatus() != PrayerStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.GONE, "This request is no longer active");
        }
        interactions.save(PrayerInteraction.builder()
                .prayerId(prayerId)
                .deviceId(deviceId)
                .build());
        p.setPrayCount(p.getPrayCount() + 1);
        return p.getPrayCount();
    }

    @Transactional(readOnly = true)
    public boolean prayedByMe(Long prayerId, String deviceId) {
        if (deviceId == null || deviceId.isBlank()) return false;
        return interactions.existsByPrayerIdAndDeviceId(prayerId, deviceId);
    }

    @Transactional
    public void deleteOwn(Long prayerId, Long memberId, String deviceId) {
        PrayerRequest p = get(prayerId);
        boolean ownByMember = memberId != null && memberId.equals(p.getAuthorMemberId());
        boolean ownByDevice = deviceId != null && !deviceId.isBlank()
                && deviceId.equals(p.getAuthorDeviceId());
        if (!ownByMember && !ownByDevice) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your request");
        }
        p.setStatus(PrayerStatus.ARCHIVED);
    }

    // ---- Admin ----

    @Transactional(readOnly = true)
    public Page<PrayerRequest> adminList(PrayerStatus status, PrayerVisibility visibility,
                                        int page, int size) {
        Pageable pg = PageRequest.of(Math.max(0, page), Math.min(100, Math.max(1, size)));
        if (status != null) {
            return requests.findByStatusOrderByCreatedAtDesc(status, pg);
        }
        if (visibility != null) {
            return requests.findByVisibilityOrderByCreatedAtDesc(visibility, pg);
        }
        return requests.findAll(pg);
    }

    @Transactional
    public PrayerRequest adminSetStatus(Long id, PrayerStatus status) {
        PrayerRequest p = get(id);
        p.setStatus(status);
        return p;
    }
}
