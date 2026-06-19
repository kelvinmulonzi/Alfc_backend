package com.example.Alfc.auth.dto;

import com.example.Alfc.auth.Member;

public record MeResponse(
        Long userId,
        String username
) {
    public static MeResponse from(Member m) {
        return new MeResponse(m.getId(), m.getUsername());
    }
}
