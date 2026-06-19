package com.example.Alfc.auth;

import com.example.Alfc.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtIssuer {

    private final SecretKey key;
    private final long ttlDays;

    public JwtIssuer(AppProperties props) {
        String secret = props.jwtSecret();
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("app.jwt-secret must be set and at least 32 characters");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttlDays = props.jwtTtlDays() != null ? props.jwtTtlDays() : 30L;
    }

    public String issue(Long memberId, String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("username", username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttlDays, ChronoUnit.DAYS)))
                .signWith(key)
                .compact();
    }

    public ParsedToken parse(String jwt) throws JwtException {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        Long memberId = Long.parseLong(claims.getSubject());
        String username = claims.get("username", String.class);
        return new ParsedToken(memberId, username);
    }

    public record ParsedToken(Long memberId, String username) {}
}
