package com.example.telegrambot.bot.callbacks;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class NoteEditCancelCallbackCodec implements CallbackCodec<Boolean> {

    private static final String PREFIX = "note_edit_cancel|";

    @Override
    public CallbackType type() {
        return CallbackType.NOTE_EDIT_CANCEL;
    }

    @Override
    public String encode(Boolean value) {
        return PREFIX + "1";
    }

    @Override
    public Optional<Boolean> decode(String data) {
        if (data == null || !data.startsWith(PREFIX)) {
            return Optional.empty();
        }

        return Optional.of(Boolean.TRUE);
    }
}
