package com.example.telegrambot.service;

import com.example.telegrambot.repository.TagRepository;
import com.example.telegrambot.service.dto.TagStat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<TagStat> getTopTags(Long chatId, int limit) {
        return tagRepository.findTagStatsByChatId(
                chatId,
                PageRequest.of(0, limit)
        );
    }

}
