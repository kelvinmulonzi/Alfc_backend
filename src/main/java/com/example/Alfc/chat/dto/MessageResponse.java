package com.example.Alfc.chat.dto;

import com.example.Alfc.chat.Message;

import java.time.Instant;

public record MessageResponse(
        Long id,
        Long senderId,
        String senderName,
        String text,
        Instant sentAt
) {
    public static MessageResponse from(Message m) {
        return new MessageResponse(
                m.getId(),
                m.getSender().getId(),
                m.getSenderName(),
                m.getText(),
                m.getCreatedAt()
        );
    }
}
