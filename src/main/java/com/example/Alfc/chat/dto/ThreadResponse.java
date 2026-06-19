package com.example.Alfc.chat.dto;

import com.example.Alfc.auth.Member;
import com.example.Alfc.chat.ChatThread;

public record ThreadResponse(
        Long id,
        PartnerResponse partner
) {
    public static ThreadResponse from(ChatThread t, Member partner) {
        return new ThreadResponse(t.getId(), PartnerResponse.from(partner));
    }
}
