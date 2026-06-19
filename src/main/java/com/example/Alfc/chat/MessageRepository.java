package com.example.Alfc.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface

MessageRepository extends JpaRepository<Message, Long> {

    @Query("select m from Message m where m.thread.id = :threadId and m.id > :afterId order by m.id asc")
    List<Message> findPageAfter(@Param("threadId") Long threadId,
                                @Param("afterId") Long afterId,
                                org.springframework.data.domain.Pageable pageable);
}
