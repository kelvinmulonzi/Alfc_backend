package com.example.Alfc.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatThreadRepository extends JpaRepository<ChatThread, Long> {

    Optional<ChatThread> findByParticipantAIdAndParticipantBId(Long aId, Long bId);

    @Query("""
        select t from ChatThread t
        where t.participantA.id = :memberId or t.participantB.id = :memberId
        order by coalesce(t.lastMessageAt, t.createdAt) desc, t.id desc
        """)
    List<ChatThread> findAllForMember(@Param("memberId") Long memberId);
}
