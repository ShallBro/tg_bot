package com.example.telegrambot.bot.view;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

public final class TagCallbacks {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private TagCallbacks() {
    }

    public static String tagNotes(String tag, int page) {
        return "tag|" + encode(tag) + "|" + page;
    }

    public static Optional<TagPagePayload> parseTagNotes(String data) {
        if (data == null || !data.startsWith("tag|")) {
            return Optional.empty();
        }

        String[] parts = data.split("\\|", 3);
        if (parts.length < 3) {
            return Optional.empty();
        }

        try {
            String tag = decode(parts[1]);
            int page = Integer.parseInt(parts[2]);
            return Optional.of(new TagPagePayload(tag, page));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static String tagsPage(int page) {
        return "tags|" + page;
    }

    public static Optional<Integer> parseTagsPage(String data) {
        if (data == null || !data.startsWith("tags|")) {
            return Optional.empty();
        }

        String[] parts = data.split("\\|", 2);
        if (parts.length < 2) {
            return Optional.empty();
        }

        try {
            return Optional.of(Integer.parseInt(parts[1]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static String encode(String tag) {
        return ENCODER.encodeToString(tag.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String encoded) {
        return new String(DECODER.decode(encoded), StandardCharsets.UTF_8);
    }

    public record TagPagePayload(String tag, int page) {
    }
}
