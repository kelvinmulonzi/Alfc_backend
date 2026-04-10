package com.example.Alfc.livestream;

import com.example.Alfc.config.AppProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Tiny YouTube Data API v3 client. Uses search.list to find an active live broadcast
 * for a channel. NOTE: search.list costs 100 quota units per call. Default daily quota
 * is 10,000, so polling more often than every ~15 minutes will burn through it.
 * That's why this is called from a single backend scheduler, not from every app instance.
 */
@Component
public class YouTubeClient {

    private final RestClient client;
    private final AppProperties props;

    public YouTubeClient(RestClient youTubeRestClient, AppProperties props) {
        this.client = youTubeRestClient;
        this.props = props;
    }

    public LiveStreamStatus fetchLiveStatus() {
        if (props.youtube().apiKey() == null || props.youtube().apiKey().isBlank()) {
            return LiveStreamStatus.offline(Instant.now());
        }

        Map<String, Object> response;
        try {
            response = client.get()
                    .uri(uri -> uri.path("/search")
                            .queryParam("part", "snippet")
                            .queryParam("channelId", props.youtube().channelId())
                            .queryParam("eventType", "live")
                            .queryParam("type", "video")
                            .queryParam("maxResults", 1)
                            .queryParam("key", props.youtube().apiKey())
                            .build())
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            return LiveStreamStatus.offline(Instant.now());
        }

        if (response == null) return LiveStreamStatus.offline(Instant.now());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
        if (items == null || items.isEmpty()) return LiveStreamStatus.offline(Instant.now());

        Map<String, Object> first = items.get(0);
        @SuppressWarnings("unchecked")
        Map<String, Object> id = (Map<String, Object>) first.get("id");
        @SuppressWarnings("unchecked")
        Map<String, Object> snippet = (Map<String, Object>) first.get("snippet");

        String videoId = id == null ? null : (String) id.get("videoId");
        if (videoId == null) return LiveStreamStatus.offline(Instant.now());

        String title = snippet == null ? null : (String) snippet.get("title");
        String thumb = null;
        if (snippet != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> thumbs = (Map<String, Object>) snippet.get("thumbnails");
            if (thumbs != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> high = (Map<String, Object>) thumbs.get("high");
                if (high != null) thumb = (String) high.get("url");
            }
        }
        Instant startedAt = null;
        if (snippet != null && snippet.get("publishedAt") != null) {
            try {
                startedAt = Instant.parse((String) snippet.get("publishedAt"));
            } catch (Exception ignored) {}
        }

        return new LiveStreamStatus(true, videoId, title, thumb, startedAt, Instant.now());
    }
}
