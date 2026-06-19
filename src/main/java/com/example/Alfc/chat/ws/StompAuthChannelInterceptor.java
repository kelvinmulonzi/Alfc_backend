package com.example.Alfc.chat.ws;

import com.example.Alfc.auth.JwtIssuer;
import com.example.Alfc.auth.MemberPrincipal;
import io.jsonwebtoken.JwtException;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * Authenticates the STOMP CONNECT frame using the same JWT that protects
 * REST endpoints (see {@link com.example.Alfc.auth.JwtAuthFilter}).
 *
 * Clients send the JWT as a native STOMP header:
 *
 *   CONNECT
 *   Authorization: Bearer &lt;jwt&gt;
 *
 * On success the parsed {@link MemberPrincipal} is attached to the session
 * via {@link StompHeaderAccessor#setUser}. Spring uses {@code principal.getName()}
 * (the member id) to route {@code /user/{id}/queue/...} destinations.
 *
 * A missing or invalid token throws and Spring sends an ERROR frame back,
 * which closes the WebSocket from the server side.
 */
@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtIssuer jwt;

    public StompAuthChannelInterceptor(JwtIssuer jwt) {
        this.jwt = jwt;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String header = accessor.getFirstNativeHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Missing Authorization header on CONNECT");
            }
            String token = header.substring("Bearer ".length()).trim();
            try {
                JwtIssuer.ParsedToken parsed = jwt.parse(token);
                MemberPrincipal principal = new MemberPrincipal(parsed.memberId(), parsed.username());
                accessor.setUser(principal);
            } catch (JwtException e) {
                throw new IllegalArgumentException("Invalid JWT on CONNECT", e);
            }
        }
        return message;
    }
}
