package com.example.Alfc.auth;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);

    List<Member> findByUsernameStartingWithIgnoreCaseOrderByUsernameAsc(String prefix, Pageable pageable);
}
