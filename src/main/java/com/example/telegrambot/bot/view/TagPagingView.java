package com.example.telegrambot.bot.view;

import com.example.telegrambot.bot.callbacks.CallbackCodec;
import com.example.telegrambot.bot.callbacks.CallbackCodecRegistry;
import com.example.telegrambot.bot.callbacks.CallbackType;
import com.example.telegrambot.bot.callbacks.MenuAction;
import com.example.telegrambot.bot.callbacks.TagPagePayload;
import com.example.telegrambot.bot.message.BotMessageService;
import com.example.telegrambot.entity.Note;
import com.example.telegrambot.service.dto.NoteSlice;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class TagPagingView {

    private final BotMessageService messages;
    private final CallbackCodec<Long> noteCallbackCodec;
    private final CallbackCodec<TagPagePayload> tagNotesCallbackCodec;
    private final CallbackCodec<MenuAction> menuCallbackCodec;

    public TagPagingView(BotMessageService messages, CallbackCodecRegistry registry) {
        this.messages = messages;
        this.noteCallbackCodec = registry.get(CallbackType.NOTE);
        this.tagNotesCallbackCodec = registry.get(CallbackType.TAG_NOTES);
        this.menuCallbackCodec = registry.get(CallbackType.MENU);
    }

    public String buildMessage(String tag, NoteSlice slice) {
        StringBuilder sb = new StringBuilder(
                messages.text("tag.command.header", tag, slice.page() + 1)
        ).append("\n\n");

        List<Note> items = slice.items();
        for (int i = 0; i < items.size(); i++) {
            Note n = items.get(i);
            sb.append(i + 1)
                    .append(") [").append(n.getId()).append("] ")
                    .append(snippet(n.getText(), 70))
                    .append("\n");
        }

        sb.append("\n")
                .append(messages.text("tag.command.footer"));
        return sb.toString();
    }

    public InlineKeyboardMarkup buildKeyboard(String tag, NoteSlice slice) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>(2);
        for (Note note : slice.items()) {
            row.add(InlineKeyboardButton.builder()
                    .text(String.valueOf(note.getId()))
                    .callbackData(noteCallbackCodec.encode(note.getId()))
                    .build());

            if (row.size() == 2) {
                keyboard.add(row);
                row = new ArrayList<>(2);
            }
        }

        if (!row.isEmpty()) {
            keyboard.add(row);
        }

        List<InlineKeyboardButton> navRow = new ArrayList<>(2);

        if (slice.hasPrev()) {
            navRow.add(InlineKeyboardButton.builder()
                    .text(messages.text("tags.nav.prev"))
                    .callbackData(tagNotesCallbackCodec.encode(new TagPagePayload(tag, slice.page() - 1)))
                    .build());
        }

        if (slice.hasNext()) {
            navRow.add(InlineKeyboardButton.builder()
                    .text(messages.text("tags.nav.next"))
                    .callbackData(tagNotesCallbackCodec.encode(new TagPagePayload(tag, slice.page() + 1)))
                    .build());
        }

        if (!navRow.isEmpty()) {
            keyboard.add(navRow);
        }

        keyboard.add(List.of(InlineKeyboardButton.builder()
                .text(messages.text("menu.button.back"))
                .callbackData(menuCallbackCodec.encode(MenuAction.BACK))
                .build()));

        return keyboard.isEmpty()
                ? null
                : InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    private String snippet(String text, int max) {
        if (text == null) {
            return "";
        }
        String t = text.replace("\n", " ").trim();
        return t.length() <= max ? t : t.substring(0, max - 1) + "...";
    }
}
