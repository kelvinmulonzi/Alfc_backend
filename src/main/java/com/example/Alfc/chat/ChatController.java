package com.example.Alfc.chat;

import com.example.Alfc.auth.MemberPrincipal;
import com.example.Alfc.chat.dto.CreateThreadRequest;
import com.example.Alfc.chat.dto.MessageResponse;
import com.example.Alfc.chat.dto.SendMessageRequest;
import com.example.Alfc.chat.dto.ThreadResponse;
import com.example.Alfc.chat.dto.ThreadSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService service;

    public ChatController(ChatService service) {
        this.service = service;
    }

    @GetMapping("/threads")
    public List<ThreadSummaryResponse> threads(@AuthenticationPrincipal MemberPrincipal me) {
        return service.listThreads(me.memberId());
    }

    @PostMapping("/threads")
    public ThreadResponse openOrCreateThread(@AuthenticationPrincipal MemberPrincipal me,
                                             @Valid @RequestBody CreateThreadRequest req) {
        return service.openThreadWith(me.memberId(), req.username());
    }

    @GetMapping("/threads/{id}/messages")
    public List<MessageResponse> messages(@AuthenticationPrincipal MemberPrincipal me,
                                          @PathVariable Long id,
                                          @RequestParam(required = false) Long afterId,
                                          @RequestParam(required = false) Integer limit) {
        return service.listMessages(me.memberId(), id, afterId, limit);
    }

    @PostMapping("/threads/{id}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse send(@AuthenticationPrincipal MemberPrincipal me,
                                @PathVariable Long id,
                                @Valid @RequestBody SendMessageRequest req) {
        return service.sendAsMember(me.memberId(), id, req.text());
    }

    @DeleteMapping("/messages/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@AuthenticationPrincipal MemberPrincipal me,
                              @PathVariable Long id) {
        service.deleteMessage(me.memberId(), id);
    }

    @PostMapping("/threads/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@AuthenticationPrincipal MemberPrincipal me,
                         @PathVariable Long id) {
        service.markRead(me.memberId(), id);
    }
}
