package com.example.telegrambot.bot.callbacks;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
public final class MenuCallbackCodec implements CallbackCodec<MenuCallbackCodec.MenuAction> {

    private static final String PREFIX = "menu|";

    @Override
    public CallbackType type() {
        return CallbackType.MENU;
    }

    @Override
    public Class<MenuAction> valueType() {
        return MenuAction.class;
    }

    @Override
    public String encode(MenuAction action) {
        return PREFIX + action.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Optional<MenuAction> decode(String data) {
        if (data == null || !data.startsWith(PREFIX)) {
            return Optional.empty();
        }

        String[] parts = data.split("\\|", 2);
        if (parts.length < 2) {
            return Optional.empty();
        }

        try {
            return Optional.of(MenuAction.valueOf(parts[1].toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public enum MenuAction {
        LAST,
        HELP,
        TAGS
    }
}
