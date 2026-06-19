package com.example.Alfc.chat.dto;

import java.time.Instant;

/**
 * Push envelope sent to {@code /user/{memberId}/queue/messages}.
 *
 * Clients switch on {@code kind} and apply the relevant update:
 *
 * - MESSAGE_NEW      → append {@code message} to thread {@code threadId}
 * - MESSAGE_DELETED  → remove {@code messageId} from thread {@code threadId}
 * - READ             → mark thread {@code threadId} as seen by {@code readerId} at {@code at}
 *
 * Only the fields relevant to {@code kind} are populated; the rest are null.
 */
public record ChatEvent(
        String kind,
        Long threadId,
        MessageResponse message,
        Long messageId,
        Long readerId,
        Instant at
) {
    public static ChatEvent newMessage(Long threadId, MessageResponse m) {
        return new ChatEvent("MESSAGE_NEW", threadId, m, null, null, null);
    }
    public static ChatEvent messageDeleted(Long threadId, Long messageId) {
        return new ChatEvent("MESSAGE_DELETED", threadId, null, messageId, null, null);
    }
    public static ChatEvent read(Long threadId, Long readerId, Instant at) {
        return new ChatEvent("READ", threadId, null, null, readerId, at);
    }
}
