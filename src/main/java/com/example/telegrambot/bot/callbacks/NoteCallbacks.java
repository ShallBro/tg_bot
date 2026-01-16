package com.example.telegrambot.bot.callbacks;

import java.util.Optional;

public final class NoteCallbacks {

    private static final String PREFIX = "note|";

    private NoteCallbacks() {
    }

    public static String note(Long id) {
        return PREFIX + id;
    }

    public static Optional<Long> parse(String data) {
        if (data == null || !data.startsWith(PREFIX)) {
            return Optional.empty();
        }

        String[] parts = data.split("\\|", 2);
        if (parts.length < 2) {
            return Optional.empty();
        }

        try {
            return Optional.of(Long.parseLong(parts[1]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
