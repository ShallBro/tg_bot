package com.example.telegrambot.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EditStateService {

    private final Map<Long, Long> editingNotes = new ConcurrentHashMap<>();

    public void start(Long chatId, Long noteId) {
        editingNotes.put(chatId, noteId);
    }

    public Optional<Long> get(Long chatId) {
        return Optional.ofNullable(editingNotes.get(chatId));
    }

    public void cancel(Long chatId) {
        editingNotes.remove(chatId);
    }
}
