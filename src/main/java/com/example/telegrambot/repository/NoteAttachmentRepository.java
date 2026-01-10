package com.example.telegrambot.repository;

import com.example.telegrambot.entity.NoteAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteAttachmentRepository extends JpaRepository<NoteAttachment, Long> {
    long countByNoteId(Long noteId);
    List<NoteAttachment> findByNoteIdOrderByIdAsc(Long noteId);
}
