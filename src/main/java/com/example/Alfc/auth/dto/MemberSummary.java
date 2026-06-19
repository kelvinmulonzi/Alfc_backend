package com.example.Alfc.auth.dto;

import com.example.Alfc.auth.Member;

public record MemberSummary(
        Long id,
        String username
) {
    public static MemberSummary from(Member m) {
        return new MemberSummary(m.getId(), m.getUsername());
    }
}
