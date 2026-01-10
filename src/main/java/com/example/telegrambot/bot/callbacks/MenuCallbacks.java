package com.example.telegrambot.bot.callbacks;

import java.util.Locale;
import java.util.Optional;

public final class MenuCallbacks {

    private static final String PREFIX = "menu|";

    private MenuCallbacks() {
    }

    public static String action(MenuAction action) {
        return PREFIX + action.name().toLowerCase(Locale.ROOT);
    }

    public static Optional<MenuAction> parse(String data) {
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
