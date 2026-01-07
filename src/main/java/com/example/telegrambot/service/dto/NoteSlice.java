package com.example.telegrambot.service.dto;

import com.example.telegrambot.entity.Note;

import java.util.List;

public record NoteSlice(List<Note> items, boolean hasNext, boolean hasPrev, int page) {
}
