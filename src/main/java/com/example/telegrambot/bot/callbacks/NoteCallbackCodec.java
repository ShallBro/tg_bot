package com.example.telegrambot.bot.callbacks;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class NoteCallbackCodec implements CallbackCodec<Long> {

    private static final String PREFIX = "note|";

    @Override
    public CallbackType type() {
        return CallbackType.NOTE;
    }

    @Override
    public Class<Long> valueType() {
        return Long.class;
    }

    @Override
    public String encode(Long id) {
        return PREFIX + id;
    }

    @Override
    public Optional<Long> decode(String data) {
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
