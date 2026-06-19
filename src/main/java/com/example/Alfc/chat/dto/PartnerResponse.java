package com.example.Alfc.chat.dto;

import com.example.Alfc.auth.Member;

public record PartnerResponse(
        Long id,
        String username
) {
    public static PartnerResponse from(Member m) {
        return new PartnerResponse(m.getId(), m.getUsername());
    }
}
