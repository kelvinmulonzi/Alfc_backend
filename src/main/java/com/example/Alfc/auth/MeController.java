package com.example.Alfc.auth;

import com.example.Alfc.auth.dto.MeResponse;
import com.example.Alfc.common.NotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class MeController {

    private final MemberRepository members;

    public MeController(MemberRepository members) {
        this.members = members;
    }

    @GetMapping
    public MeResponse get(@AuthenticationPrincipal MemberPrincipal me) {
        Member m = members.findById(me.memberId())
                .orElseThrow(() -> new NotFoundException("Member not found"));
        return MeResponse.from(m);
    }
}
