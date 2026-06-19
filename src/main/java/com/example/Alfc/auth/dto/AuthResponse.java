package com.example.Alfc.auth.dto;

import com.example.Alfc.auth.Member;

public record AuthResponse(
        String accessToken,
        Long userId,
        String username
) {
    public static AuthResponse of(Member m, String token) {
        return new AuthResponse(token, m.getId(), m.getUsername());
    }
}
