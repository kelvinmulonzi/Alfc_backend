package com.example.Alfc.auth;

import com.example.Alfc.auth.dto.AuthResponse;
import com.example.Alfc.auth.dto.LoginRequest;
import com.example.Alfc.auth.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final MemberRepository members;
    private final PasswordEncoder passwordEncoder;
    private final JwtIssuer jwt;

    public AuthService(MemberRepository members,
                       PasswordEncoder passwordEncoder,
                       JwtIssuer jwt) {
        this.members = members;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String username = req.username().trim();
        if (members.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is taken");
        }
        Member m = members.save(Member.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(req.password()))
                .build());
        return AuthResponse.of(m, jwt.issue(m.getId(), m.getUsername()));
    }

    public AuthResponse login(LoginRequest req) {
        Member m = members.findByUsernameIgnoreCase(req.username().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(req.password(), m.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return AuthResponse.of(m, jwt.issue(m.getId(), m.getUsername()));
    }
}
