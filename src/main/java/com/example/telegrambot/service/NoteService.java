package com.example.telegrambot.service;

import com.example.telegrambot.entity.Note;
import com.example.telegrambot.entity.Tag;
import com.example.telegrambot.repository.NoteRepository;
import com.example.telegrambot.repository.TagRepository;
import com.example.telegrambot.utils.TagParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepo;
    private final TagRepository tagRepo;
    private final TagParser tagParser;

    @Transactional
    public void saveTextNote(Long chatId, Long messageId, String text) {
        Note note = new Note();
        note.setChatId(chatId);
        note.setMessageId(messageId);
        note.setText(text);

        Set<String> tags = tagParser.parse(text);
        for (String tag : tags) {
            Tag tagEntity = tagRepo.findByName(tag).orElseGet(() -> tagRepo.save(new Tag(tag)));
            note.getTags().add(tagEntity);
        }

        noteRepo.save(note);
    }

    @Transactional(readOnly = true)
    public Note last(Long chatId) {
        return noteRepo.findTopByChatIdOrderByCreatedAtDesc(chatId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Note> byTag(Long chatId, String tag, int limit) {
        return noteRepo.findByChatAndTag(chatId, tag.toLowerCase(), PageRequest.of(0, limit)).getContent();
    }
}
