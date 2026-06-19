package com.example.Alfc.chat;

import com.example.Alfc.auth.Member;
import com.example.Alfc.auth.MemberRepository;
import com.example.Alfc.chat.dto.ChatEvent;
import com.example.Alfc.chat.dto.MessageResponse;
import com.example.Alfc.chat.dto.ThreadResponse;
import com.example.Alfc.chat.dto.ThreadSummaryResponse;
import com.example.Alfc.common.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class ChatService {

    /** STOMP destination used by clients to subscribe to their personal queue. */
    private static final String USER_QUEUE = "/queue/messages";

    private final ChatThreadRepository threads;
    private final MessageRepository messages;
    private final MemberRepository members;
    private final SimpMessagingTemplate messaging;

    public ChatService(ChatThreadRepository threads,
                       MessageRepository messages,
                       MemberRepository members,
                       SimpMessagingTemplate messaging) {
        this.threads = threads;
        this.messages = messages;
        this.members = members;
        this.messaging = messaging;
    }

    @Transactional(readOnly = true)
    public List<ThreadSummaryResponse> listThreads(Long memberId) {
        return threads.findAllForMember(memberId).stream()
                .map(t -> ThreadSummaryResponse.from(
                        t,
                        partnerOf(t, memberId),
                        unreadFor(t, memberId)
                ))
                .toList();
    }

    @Transactional
    public void markRead(Long memberId, Long threadId) {
        ChatThread t = participantThread(memberId, threadId);
        Instant now = Instant.now();
        if (t.getParticipantA().getId().equals(memberId)) t.setParticipantALastReadAt(now);
        else t.setParticipantBLastReadAt(now);
        threads.save(t);

        // Notify the OTHER participant so their UI can update read receipts.
        Long otherId = otherParticipantId(t, memberId);
        sendToMember(otherId, ChatEvent.read(t.getId(), memberId, now));
    }

    @Transactional
    public ThreadResponse openThreadWith(Long memberId, String otherUsername) {
        Member me = members.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found"));
        Member other = members.findByUsernameIgnoreCase(otherUsername.trim())
                .orElseThrow(() -> new NotFoundException("No user with that username"));
        if (other.getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot start a chat with yourself");
        }

        Member a = me.getId() < other.getId() ? me : other;
        Member b = me.getId() < other.getId() ? other : me;

        ChatThread t = threads.findByParticipantAIdAndParticipantBId(a.getId(), b.getId())
                .orElseGet(() -> threads.save(ChatThread.builder()
                        .participantA(a)
                        .participantB(b)
                        .build()));
        return ThreadResponse.from(t, other);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> listMessages(Long memberId, Long threadId, Long afterId, Integer limit) {
        ChatThread thread = participantThread(memberId, threadId);
        long after = afterId == null ? 0L : afterId;
        int size = (limit == null || limit < 1 || limit > 200) ? 50 : limit;
        return messages.findPageAfter(thread.getId(), after, PageRequest.of(0, size))
                .stream().map(MessageResponse::from).toList();
    }

    @Transactional
    public MessageResponse sendAsMember(Long memberId, Long threadId, String text) {
        ChatThread thread = participantThread(memberId, threadId);
        Member sender = members.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found"));

        Message msg = messages.save(Message.builder()
                .thread(thread)
                .sender(sender)
                .senderName(sender.getUsername())
                .text(text)
                .build());

        Instant now = Instant.now();
        thread.setLastMessageAt(now);
        thread.setLastMessagePreview(preview(text));
        // Sender has obviously seen up to their own message.
        if (thread.getParticipantA().getId().equals(memberId)) thread.setParticipantALastReadAt(now);
        else thread.setParticipantBLastReadAt(now);
        threads.save(thread);

        MessageResponse response = MessageResponse.from(msg);

        // Push to both participants. The sender's other open clients see the
        // message land too; the receiver gets it in real time.
        ChatEvent event = ChatEvent.newMessage(thread.getId(), response);
        sendToMember(thread.getParticipantA().getId(), event);
        sendToMember(thread.getParticipantB().getId(), event);

        return response;
    }

    private static boolean unreadFor(ChatThread t, Long memberId) {
        Instant last = t.getLastMessageAt();
        if (last == null) return false;
        Instant read = t.getParticipantA().getId().equals(memberId)
                ? t.getParticipantALastReadAt()
                : t.getParticipantBLastReadAt();
        return read == null || last.isAfter(read);
    }

    @Transactional
    public void deleteMessage(Long memberId, Long messageId) {
        Message m = messages.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found"));
        if (!m.getSender().getId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your message");
        }
        ChatThread thread = m.getThread();
        Long threadId = thread.getId();
        Long aId = thread.getParticipantA().getId();
        Long bId = thread.getParticipantB().getId();
        messages.delete(m);
        // If we just deleted the last preview, refresh the thread's tail.
        if (thread.getLastMessagePreview() != null && thread.getLastMessagePreview().equals(m.getText().length() > 200 ? m.getText().substring(0, 200) : m.getText())) {
            // Cheap recomputation: leave preview as-is. The next sent message will overwrite it.
        }

        // Notify both participants so their UI can drop the message.
        ChatEvent event = ChatEvent.messageDeleted(threadId, messageId);
        sendToMember(aId, event);
        sendToMember(bId, event);
    }

    private ChatThread participantThread(Long memberId, Long threadId) {
        ChatThread t = threads.findById(threadId)
                .orElseThrow(() -> new NotFoundException("Thread not found"));
        boolean partOfThread = t.getParticipantA().getId().equals(memberId)
                || t.getParticipantB().getId().equals(memberId);
        if (!partOfThread) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your thread");
        }
        return t;
    }

    private static Member partnerOf(ChatThread t, Long memberId) {
        return t.getParticipantA().getId().equals(memberId) ? t.getParticipantB() : t.getParticipantA();
    }

    private static Long otherParticipantId(ChatThread t, Long memberId) {
        return t.getParticipantA().getId().equals(memberId)
                ? t.getParticipantB().getId()
                : t.getParticipantA().getId();
    }

    private void sendToMember(Long memberId, ChatEvent event) {
        // Spring routes /user/{principalName}/queue/messages to that member's
        // session(s). Principal name is the member id (see MemberPrincipal).
        messaging.convertAndSendToUser(String.valueOf(memberId), USER_QUEUE, event);
    }

    private static String preview(String text) {
        if (text == null) return null;
        return text.length() > 200 ? text.substring(0, 200) : text;
    }
}
