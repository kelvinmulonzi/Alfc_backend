package com.example.Alfc.chat.dto;

import com.example.Alfc.auth.Member;
import com.example.Alfc.chat.ChatThread;

import java.time.Instant;

public record ThreadSummaryResponse(
        Long id,
        PartnerResponse partner,
        String lastMessage,
        Instant lastMessageAt,
        boolean unread
) {
    public static ThreadSummaryResponse from(ChatThread t, Member partner, boolean unread) {
        return new ThreadSummaryResponse(
                t.getId(),
                PartnerResponse.from(partner),
                t.getLastMessagePreview(),
                t.getLastMessageAt(),
                unread
        );
    }
}
