package com.example.Alfc.auth;

import com.example.Alfc.auth.dto.MemberSummary;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MembersController {

    private static final int MAX_RESULTS = 20;

    private final MemberRepository members;

    public MembersController(MemberRepository members) {
        this.members = members;
    }

    /** Prefix-search registered usernames, excluding the caller. */
    @GetMapping("/search")
    public List<MemberSummary> search(@AuthenticationPrincipal MemberPrincipal me,
                                      @RequestParam("q") String query) {
        String q = query == null ? "" : query.trim();
        if (q.isEmpty()) return List.of();
        return members.findByUsernameStartingWithIgnoreCaseOrderByUsernameAsc(
                        q, PageRequest.of(0, MAX_RESULTS)).stream()
                .filter(m -> !m.getId().equals(me.memberId()))
                .map(MemberSummary::from)
                .toList();
    }
}
