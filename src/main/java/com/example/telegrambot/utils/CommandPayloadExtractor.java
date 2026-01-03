package com.example.telegrambot.utils;

import java.util.Optional;
import java.util.regex.Pattern;


public final class CommandPayloadExtractor {

    private CommandPayloadExtractor() {}

    public static Optional<String> extract(String text, String command) {
        if (text == null) return Optional.empty();

        // command ожидаем вида "/search", "/note" и т.п.
        String cmd = Pattern.quote(command);

        String payload = text.replaceFirst("^" + cmd + "(@\\w+)?\\s*", "")
                .trim();

        return payload.isBlank() ? Optional.empty() : Optional.of(payload);
    }
}
