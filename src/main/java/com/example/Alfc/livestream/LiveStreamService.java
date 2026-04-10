package com.example.Alfc.livestream;

import com.example.Alfc.config.AppProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Caches the current YouTube live status. The Android app hits this service via
 * the controller; we never let the app hit YouTube directly. The scheduler refreshes
 * the cache once per minute, regardless of how many app instances are connected.
 */
@Service
public class LiveStreamService {

    private static final Logger log = LoggerFactory.getLogger(LiveStreamService.class);

    private final YouTubeClient youTubeClient;
    private final AppProperties props;
    private final AtomicReference<LiveStreamStatus> cache =
            new AtomicReference<>(LiveStreamStatus.offline(Instant.EPOCH));

    public LiveStreamService(YouTubeClient youTubeClient, AppProperties props) {
        this.youTubeClient = youTubeClient;
        this.props = props;
    }

    public LiveStreamStatus current() {
        return cache.get();
    }

    @PostConstruct
    void warmUp() {
        refresh();
    }

    @Scheduled(fixedDelayString = "${app.youtube.poll-interval-ms:60000}")
    public void refresh() {
        try {
            LiveStreamStatus fresh = youTubeClient.fetchLiveStatus();
            cache.set(fresh);
            log.debug("Live status refreshed: live={} videoId={}", fresh.live(), fresh.videoId());
        } catch (Exception e) {
            log.warn("Failed to refresh live status: {}", e.getMessage());
        }
    }
}
