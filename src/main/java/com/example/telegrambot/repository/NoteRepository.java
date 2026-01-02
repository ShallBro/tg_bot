package com.example.telegrambot.repository;

import com.example.telegrambot.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    Optional<Note> findTopByChatIdOrderByCreatedAtDesc(Long chatId);

    List<Note> findByChatIdOrderByCreatedAtDesc(Long chatId, Pageable pageable);

    @Query("""
        select distinct n
        from Note n
        join n.tags t
        where n.chatId = :chatId
          and t.name = :tag
        order by n.createdAt desc
        """)
    Page<Note> findByChatAndTag(
            @Param("chatId") Long chatId,
            @Param("tag") String tag,
            Pageable pageable
    );
}
