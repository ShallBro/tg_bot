package com.example.telegrambot.repository;

import com.example.telegrambot.entity.Tag;
import com.example.telegrambot.service.dto.TagStat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    @Query("""
    select new com.example.telegrambot.service.dto.TagStat(
        t.id,
        t.name,
        count(distinct n.id)
    )
    from Note n
    join n.tags t
    where n.chatId = :chatId
    group by t.id, t.name
    order by count(distinct n.id) desc, t.name asc
    """)
    List<TagStat> findTagStatsByChatId(Long chatId, Pageable pageable);
}
