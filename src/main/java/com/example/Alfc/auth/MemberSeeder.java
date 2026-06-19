package com.example.Alfc.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class MemberSeeder {

    private static final Logger log = LoggerFactory.getLogger(MemberSeeder.class);
    private static final String SEED_PASSWORD = "Passw0rd!";
    private static final List<String> SEED_USERNAMES =
            List.of("alice", "mary", "paul", "john", "ruth");

    @Bean
    public ApplicationRunner seedMembers(MemberRepository members, PasswordEncoder encoder) {
        return args -> {
            String hash = encoder.encode(SEED_PASSWORD);
            int created = 0;
            for (String username : SEED_USERNAMES) {
                if (members.existsByUsernameIgnoreCase(username)) continue;
                members.save(Member.builder()
                        .username(username)
                        .passwordHash(hash)
                        .build());
                created++;
            }
            if (created > 0) {
                log.info("=== Seeded {} test members ===", created);
                log.info("    usernames: {}", SEED_USERNAMES);
                log.info("    password : {}", SEED_PASSWORD);
                log.info("==================================");
            }
        };
    }
}
