package com.example.telegrambot.service;

import com.example.telegrambot.entity.Note;
import com.example.telegrambot.entity.Tag;
import com.example.telegrambot.repository.NoteRepository;
import com.example.telegrambot.repository.TagRepository;
import com.example.telegrambot.service.dto.NoteSlice;
import com.example.telegrambot.utils.TagParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;
    private final TagParser tagParser;


    public NoteSlice findByTagPaged(Long chatId, String tag, int page, int size) {
        int fetch = size + 1;
        var list = noteRepository.findByChatAndTag(chatId, tag, PageRequest.of(page, fetch));

        boolean hasNext = list.size() > size;
        boolean hasPrev = page > 0;

        var items = hasNext ? list.subList(0, size) : list;
        return new NoteSlice(items, hasNext, hasPrev, page);
    }

    @Transactional
    public Note saveTextNote(Long chatId, Long messageId, String text) {
        Note note = new Note();
        note.setChatId(chatId);
        note.setMessageId(messageId);
        note.setText(text);

        Set<String> tags = tagParser.parse(text);
        for (String tag : tags) {
            Tag tagEntity = tagRepository.findByName(tag).orElseGet(() -> tagRepository.save(new Tag(tag)));
            note.getTags().add(tagEntity);
        }

        return noteRepository.save(note);
    }

    @Transactional(readOnly = true)
    public Note last(Long chatId) {
        return noteRepository.findTopByChatIdOrderByCreatedAtDesc(chatId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Note> findNotes(Long chatId, String query) {
        return noteRepository.findByChatIdAndTextContains(
                chatId,
                query);
    }

    @Transactional(readOnly = true)
    public Optional<Note> findNote(Long chatId, Long id) {
        return noteRepository.findByIdAndChatId(id, chatId);
    }

    @Transactional
    public boolean delete(Long chatId, Long id) {
        var note = noteRepository.findByIdAndChatId(id, chatId);
        if (note.isEmpty()) return false;
        noteRepository.delete(note.get());
        return true;
    }
}
