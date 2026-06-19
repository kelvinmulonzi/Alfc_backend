package com.example.Alfc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String adminToken,
        String jwtSecret,
        Long jwtTtlDays,
        YouTube youtube
) {
    public record YouTube(
            String apiKey,
            String channelId,
            long pollIntervalMs
    ) {}
}
