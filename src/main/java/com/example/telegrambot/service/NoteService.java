package com.example.telegrambot.service;

import com.example.telegrambot.entity.Note;
import com.example.telegrambot.entity.NoteAttachment;
import com.example.telegrambot.entity.Tag;
import com.example.telegrambot.repository.NoteAttachmentRepository;
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

    private static final int MAX_ATTACHMENTS = 10;

    private final NoteRepository noteRepository;
    private final NoteAttachmentRepository noteAttachmentRepository;
    private final TagRepository tagRepository;
    private final TagParser tagParser;


    public NoteSlice findByTagIdPaged(Long chatId, Long tagId, int page, int size) {
        int fetch = size + 1;
        var list = noteRepository.findLatestByTagId(chatId, tagId, PageRequest.of(page, fetch));

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

        applyTags(note, text);

        return noteRepository.save(note);
    }

    @Transactional
    public Note saveMediaNote(Long chatId,
                              Long messageId,
                              String text,
                              String mediaGroupId,
                              List<NoteAttachmentPayload> attachments) {
        Note note = resolveMediaNote(chatId, messageId, text, mediaGroupId);

        if (attachments == null || attachments.isEmpty()) {
            return note;
        }

        long existingCount = noteAttachmentRepository.countByNoteId(note.getId());
        int allowed = Math.max(0, MAX_ATTACHMENTS - (int) existingCount);
        if (allowed == 0) {
            return note;
        }

        List<NoteAttachmentPayload> bounded = attachments.size() > allowed
                ? attachments.subList(0, allowed)
                : attachments;

        List<NoteAttachment> entities = bounded.stream()
                .map(payload -> toAttachment(note, payload))
                .toList();

        noteAttachmentRepository.saveAll(entities);
        return note;
    }

    @Transactional(readOnly = true)
    public List<NoteAttachment> getAttachments(Long noteId) {
        return noteAttachmentRepository.findByNoteIdOrderByIdAsc(noteId);
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
    public boolean updateTextNote(Long chatId, Long id, String text) {
        var note = noteRepository.findByIdAndChatId(id, chatId);
        if (note.isEmpty()) {
            return false;
        }

        Note entity = note.get();
        entity.setText(text);
        entity.getTags().clear();
        applyTags(entity, text);
        noteRepository.save(entity);
        return true;
    }

    @Transactional
    public boolean delete(Long chatId, Long id) {
        var note = noteRepository.findByIdAndChatId(id, chatId);
        if (note.isEmpty()) return false;
        noteRepository.delete(note.get());
        return true;
    }

    private Note resolveMediaNote(Long chatId, Long messageId, String text, String mediaGroupId) {
        Note note;
        if (mediaGroupId != null && !mediaGroupId.isBlank()) {
            note = noteRepository.findByChatIdAndMediaGroupId(chatId, mediaGroupId)
                    .orElseGet(() -> {
                        Note fresh = new Note();
                        fresh.setChatId(chatId);
                        fresh.setMessageId(messageId);
                        fresh.setMediaGroupId(mediaGroupId);
                        fresh.setText(text);
                        return fresh;
                    });
        } else {
            note = new Note();
            note.setChatId(chatId);
            note.setMessageId(messageId);
            note.setText(text);
        }

        if (note.getId() == null) {
            applyTags(note, text);
            return noteRepository.save(note);
        }

        if (note.getText() == null || note.getText().isBlank()) {
            if (text != null && !text.isBlank()) {
                note.setText(text);
                applyTags(note, text);
                noteRepository.save(note);
            }
        }

        return note;
    }

    private void applyTags(Note note, String text) {
        Set<String> tags = tagParser.parse(text);
        for (String tag : tags) {
            Tag tagEntity = tagRepository.findByName(tag).orElseGet(() -> tagRepository.save(new Tag(tag)));
            note.getTags().add(tagEntity);
        }
    }

    public record NoteAttachmentPayload(
            String type,
            String fileId,
            String fileUniqueId,
            String fileName,
            String mimeType,
            Long fileSize
    ) {
    }

    private NoteAttachment toAttachment(Note note, NoteAttachmentPayload payload) {
        NoteAttachment attachment = new NoteAttachment();
        attachment.setNote(note);
        attachment.setType(payload.type());
        attachment.setFileId(payload.fileId());
        attachment.setFileUniqueId(payload.fileUniqueId());
        attachment.setFileName(payload.fileName());
        attachment.setMimeType(payload.mimeType());
        attachment.setFileSize(payload.fileSize());
        return attachment;
    }
}
