package com.example.telegrambot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table(name = "note_attachments")
@Getter
@Setter
public class NoteAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @Column(name = "type", nullable = false, length = 16)
    private String type;

    @Column(name = "file_id", nullable = false, columnDefinition = "text")
    private String fileId;

    @Column(name = "file_unique_id", columnDefinition = "text")
    private String fileUniqueId;

    @Column(name = "file_name", columnDefinition = "text")
    private String fileName;

    @Column(name = "mime_type", columnDefinition = "text")
    private String mimeType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();
}
