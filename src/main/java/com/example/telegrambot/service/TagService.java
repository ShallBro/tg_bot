package com.example.telegrambot.service;

import com.example.telegrambot.repository.TagRepository;
import com.example.telegrambot.service.dto.TagSlice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public TagSlice getTags(Long chatId, int page, int size) {
        int fetchSize = size + 1;
        var list = tagRepository.findTagStatsByChatId(
                chatId,
                PageRequest.of(page, fetchSize)
        );

        boolean hasNext = list.size() > size;
        var items = hasNext ? list.subList(0, size) : list;

        return new TagSlice(items, hasNext, page > 0, page, size);
    }
}
