package com.example.telegrambot.bot.callbacks;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class TagsPageCallbackCodec implements CallbackCodec<Integer> {

    private static final String PREFIX = "tags|";

    @Override
    public CallbackType type() {
        return CallbackType.TAGS_PAGE;
    }

    @Override
    public Class<Integer> valueType() {
        return Integer.class;
    }

    @Override
    public String encode(Integer page) {
        return PREFIX + page;
    }

    @Override
    public Optional<Integer> decode(String data) {
        if (data == null || !data.startsWith(PREFIX)) {
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
}
