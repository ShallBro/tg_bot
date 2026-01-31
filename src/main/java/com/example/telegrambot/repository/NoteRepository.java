package com.example.telegrambot.repository;

import com.example.telegrambot.entity.Note;
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

    Optional<Note> findByIdAndChatId(Long id, Long chatId);

    Optional<Note> findByChatIdAndMediaGroupId(Long chatId, String mediaGroupId);

    @Query("""
            select distinct n
            from Note n
            join n.tags t
            where n.chatId = :chatId
              and t.name = :tag
            order by n.createdAt desc
            """)
    List<Note> findByChatAndTag(
            @Param("chatId") Long chatId,
            @Param("tag") String tag,
            Pageable pageable
    );

    @Query("""
            select n
            from Note n
            where n.chatId = :chatId
              and lower(n.text) like lower(concat('%', :q, '%'))
            order by n.createdAt desc
            """)
    List<Note> findByChatIdAndTextContains(@Param("chatId") Long chatId,
                                           @Param("q") String query);

    @Query("""
    select distinct n
    from Note n
    join n.tags t
    where n.chatId = :chatId
      and t.id = :tagId
    order by n.createdAt desc
    """)
    List<Note> findLatestByTagId(Long chatId, Long tagId, Pageable pageable);

}
