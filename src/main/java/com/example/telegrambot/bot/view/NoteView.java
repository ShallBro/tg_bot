package com.example.telegrambot.bot.view;

import com.example.telegrambot.bot.callbacks.CallbackCodec;
import com.example.telegrambot.bot.callbacks.CallbackCodecRegistry;
import com.example.telegrambot.bot.callbacks.CallbackType;
import com.example.telegrambot.bot.message.BotMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class NoteView {

    private final BotMessageService messages;
    private final CallbackCodec<Long> noteEditCallbackCodec;
    private final CallbackCodec<Boolean> noteEditCancelCallbackCodec;

    public NoteView(BotMessageService messages, CallbackCodecRegistry registry) {
        this.messages = messages;
        this.noteEditCallbackCodec = registry.get(CallbackType.NOTE_EDIT);
        this.noteEditCancelCallbackCodec = registry.get(CallbackType.NOTE_EDIT_CANCEL);
    }

    public InlineKeyboardMarkup buildNoteKeyboard(Long noteId) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(
                        InlineKeyboardButton.builder()
                                .text(messages.text("note.button.edit"))
                                .callbackData(noteEditCallbackCodec.encode(noteId))
                                .build()
                )))
                .build();
    }

    public InlineKeyboardMarkup buildEditKeyboard() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(
                        InlineKeyboardButton.builder()
                                .text(messages.text("note.button.cancel"))
                                .callbackData(noteEditCancelCallbackCodec.encode(true))
                                .build()
                )))
                .build();
    }
}
