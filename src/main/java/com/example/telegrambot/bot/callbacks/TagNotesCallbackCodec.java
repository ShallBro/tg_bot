package com.example.telegrambot.bot.callbacks;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Component
public final class TagNotesCallbackCodec implements CallbackCodec<TagPagePayload> {

    private static final String PREFIX = "tag|";
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    @Override
    public CallbackType type() {
        return CallbackType.TAG_NOTES;
    }

    @Override
    public Class<TagPagePayload> valueType() {
        return TagPagePayload.class;
    }

    @Override
    public String encode(TagPagePayload payload) {
        return PREFIX + encodeTag(payload.tag()) + "|" + payload.page();
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
            String tag = decodeTag(parts[1]);
            int page = Integer.parseInt(parts[2]);
            return Optional.of(new TagPagePayload(tag, page));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private String encodeTag(String tag) {
        return ENCODER.encodeToString(tag.getBytes(StandardCharsets.UTF_8));
    }

    private String decodeTag(String encoded) {
        return new String(DECODER.decode(encoded), StandardCharsets.UTF_8);
    }
}
