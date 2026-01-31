package com.example.telegrambot.bot.callbacks;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Component
public final class TagNotesCallbackCodec implements CallbackCodec<TagPagePayload> {

    private static final String PREFIX = "tag|";

    @Override
    public CallbackType type() {
        return CallbackType.TAG_NOTES;
    }

    @Override
    public String encode(TagPagePayload payload) {
        return PREFIX + payload.tagId() + "|" + payload.page();
    }

    @Override
    public Optional<TagPagePayload> decode(String data) {
        if (data == null || !data.startsWith(PREFIX)) {
            return Optional.empty();
        }

        String[] parts = data.split("\\|", 3);
        if (parts.length < 3) {
            return Optional.empty();
        }

        try {
            long tagId = Long.parseLong(parts[1]);
            int page = Integer.parseInt(parts[2]);
            return Optional.of(new TagPagePayload(tagId, page));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
