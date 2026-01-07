package com.example.telegrambot.bot.view;

import com.example.telegrambot.entity.Note;
import com.example.telegrambot.service.dto.NoteSlice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public final class TagPagingView {

    private TagPagingView() {}

    public static String buildMessage(String tag, NoteSlice slice) {
        StringBuilder sb = new StringBuilder();
        sb.append("🏷️ *#").append(tag).append("* — страница ").append(slice.page() + 1).append("\n\n");

        List<Note> items = slice.items();
        for (int i = 0; i < items.size(); i++) {
            Note n = items.get(i);
            sb.append(i + 1)
                    .append(") [").append(n.getId()).append("] ")
                    .append(snippet(n.getText(), 70))
                    .append("\n");
        }

        sb.append("\nОткрыть: `/note <id>`  •  Удалить: `/delete <id>`");
        return sb.toString();
    }

    public static InlineKeyboardMarkup buildKeyboard(String tag, NoteSlice slice) {
        List<InlineKeyboardButton> row = new ArrayList<>();

        if (slice.hasPrev()) {
            row.add(InlineKeyboardButton.builder()
                    .text("◀️")
                    .callbackData("tag|" + tag + "|" + (slice.page() - 1))
                    .build());
        }

        if (slice.hasNext()) {
            row.add(InlineKeyboardButton.builder()
                    .text("▶️")
                    .callbackData("tag|" + tag + "|" + (slice.page() + 1))
                    .build());
        }

        if (row.isEmpty()) return null;

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .build();
    }

    private static String snippet(String text, int max) {
        if (text == null) return "";
        String t = text.replace("\n", " ").trim();
        return t.length() <= max ? t : t.substring(0, max - 1) + "…";
    }
}
