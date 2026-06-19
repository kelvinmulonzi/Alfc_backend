package com.example.Alfc.auth;

import java.security.Principal;

/**
 * The authenticated member, attached to every authenticated request and
 * every STOMP CONNECT.
 *
 * Implements {@link Principal} so Spring's user-destination resolver can
 * route messages addressed to {@code /user/{memberId}/queue/...} back to
 * this member's WebSocket session. {@code memberId} is used as the
 * principal name because it is immutable; usernames can be edited.
 */
public record MemberPrincipal(Long memberId, String username) implements Principal {
    @Override
    public String getName() {
        return String.valueOf(memberId);
    }
}
